pipeline {
  agent any


  triggers{
    githubPush()
  }

  tools {
      maven 'Maven 3.9.11'
  }

  environment {
    MVN_CMD = "mvn"
//     SONAR_HOST_URL = "http://<seu-sonar-host>:9000"
//     SONAR_LOGIN = credentials('sonar-token-id')  // credencial armazenada no Jenkins
  }

//   stages {
//     stage('Checkout') {
//       steps {
//         checkout scm
//       }
//     }

stages{
stage('Checkout') {
  steps {
    echo '📥 A fazer checkout do repositório...'
    git url: 'https://github.com/pfcosta02/mei-arqsoft-odsoft.git', branch: 'main'
  }
}

    stage('Build & Compile') {
      steps {
          echo '🚀 A iniciar o build...'
          bat '${MVN_CMD} clean compile -B'
      }
    }
//
//     stage('Run Unit Tests') {
//       steps {
//         bat "${MVN_CMD} test -B"
//       }
//       post {
//         always {
//           junit '**/target/surefire-reports/*.xml'
//         }
//       }
//     }

    stage('Code Quality / SonarQube Analysis') {
            steps {
                script {

                    withSonarQubeEnv(installationName: 'Sonarqube') {
                     bat 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:4.0.0.4121:sonar'

                }
            }
        }
    }

    stage('Package') {
      steps {
        echo 'Gerando artefato...'
        bat "${MVN_CMD} package -B -DskipTests"
//         archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
      }
    }

//     stage('Deploy to Dev') {
//       steps {
//         // Por exemplo via SSH ou usando docker pull + docker run no servidor
//         sshagent(['ssh-credentials-id']) {
//           sh """
//             ssh user@dev-server "docker pull registry.exemplo.com/${IMAGE_NAME} && docker stop odsoft || true && docker rm odsoft || true && docker run -d --name odsoft -p 8080:8080 registry.exemplo.com/${IMAGE_NAME}"
//           """
//         }
//       }
//     }
  }

  post {
    always {
      echo '🏁 Pipeline terminada!'
//       archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
//       cleanWs()
    }
    failure {
    echo "Error na pipeline!"
//       mail to: 'teu-email@dominio.com',
//            subject: "Build FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
//            body: "Veja no Jenkins console output: ${env.BUILD_URL}"
    }
  }
}
