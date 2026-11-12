pipeline {
  agent any
  options { disableConcurrentBuilds() }
  environment {
    REGISTRY      = 'expensereghash123.azurecr.io'
    IMAGE         = 'todo-tracker'
    SONAR_ORG     = 'harshithamandala'
    SONAR_PROJECT = 'HARSHITHAMANDALA_todo-tracker'
  }
  stages {
    stage('Checkout') {
      steps {
        checkout scm
        script { env.TAG = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim() }
      }
    }
    stage('Secret Scan (Gitleaks)') {
      steps { sh 'gitleaks detect --source . --no-git --redact --verbose' }
    }
    stage('Build & Test') {
      steps { sh 'mvn -B clean verify' }
      post { always { junit 'target/surefire-reports/*.xml' } }
    }
    stage('SonarCloud Analysis') {
      steps {
        withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
          sh '''mvn -B org.sonarsource.scanner.maven:sonar-maven-plugin:5.0.0.4389:sonar \
            -Dsonar.host.url=https://sonarcloud.io \
            -Dsonar.organization=$SONAR_ORG \
            -Dsonar.projectKey=$SONAR_PROJECT \
            -Dsonar.token=$SONAR_TOKEN \
            -Dsonar.qualitygate.wait=true'''
        }
      }
    }
    stage('Docker Build') {
      steps { sh 'docker build -t $REGISTRY/$IMAGE:$TAG -t $REGISTRY/$IMAGE:latest .' }
    }
    stage('Push to ACR') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'acr-creds', usernameVariable: 'ACR_USER', passwordVariable: 'ACR_PASS')]) {
          sh '''echo "$ACR_PASS" | docker login $REGISTRY -u "$ACR_USER" --password-stdin
            docker push $REGISTRY/$IMAGE:$TAG
            docker push $REGISTRY/$IMAGE:latest
            docker logout $REGISTRY'''
        }
      }
    }
    stage('Deploy to k3s (Ansible)') {
      steps { sh 'ansible-playbook -i localhost, -c local ansible/deploy.yml -e image=$REGISTRY/$IMAGE:$TAG' }
    }
    stage('Smoke Test') {
      steps {
        retry(5) {
          sleep 5
          sh 'curl -fsS http://127.0.0.1:30080/'
        }
      }
    }
  }
  post {
    success { archiveArtifacts artifacts: 'target/*.jar' }
  }
}
