pipeline {
  agent {
    kubernetes {
      yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: shell
    image: teammates:1.0 
    command:
    - sleep
    args:
    - infinity
'''
      defaultContainer 'shell'
    }

  }
  stages {
    stage('Create config') {
      steps {
        withGradle() {
          sh '''./gradlew --no-daemon createConfigs
npm ci'''
        }

      }
    }

    stage('Build Web UI') {
      steps {
        withGradle() {
          sh '''./gradlew --no-daemon generateTypes
npm run build'''
        }

      }
    }

  }
}