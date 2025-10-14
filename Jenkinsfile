pipeline {
  agent any

  tools {
      maven 'Maven 3.9.11'
  }

  environment {
    // Define variáveis comuns; ajusta se usares outro Sonar host ou credenciais
    MVN_CMD = "mvn"  // usa wrapper para garantir versão
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
    git url: 'https://github.com/pfcosta02/mei-arqsoft-odsoft.git', branch: 'main'
  }
}

    stage('Build & Compile') {
      steps {
        bat "${MVN_CMD} clean compile -B"
      }
    }

    stage('Run Unit Tests') {
      steps {
        bat "${MVN_CMD} test -B"
      }
      post {
        always {
          junit '**/target/surefire-reports/*.xml'
        }
      }
    }

//     stage('Code Quality / Sonar') {
//       steps {
//         // Executa análise Sonar (verifica duplicações, bugs, smells)
//         bat """
//           ${MVN_CMD} sonar:sonar \
//             -Dsonar.projectKey=ODSoft \
//             -Dsonar.host.url=${SONAR_HOST_URL} \
//             -Dsonar.login=${SONAR_LOGIN}
//         """
//       }
//     }

    stage('Package') {
      steps {
        bat "${MVN_CMD} package -B -DskipTests"
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
      archiveArtifacts artifacts: '**/target/*.jar', allowEmptyArchive: true
      cleanWs()
    }
    failure {
    echo "Erro na pipeline"
//       mail to: 'teu-email@dominio.com',
//            subject: "Build FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
//            body: "Veja no Jenkins console output: ${env.BUILD_URL}"
    }
  }
}
