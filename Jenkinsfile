pipeline {
    agent any

    triggers {
        githubPush()
    }

    tools {
        maven 'Maven 3.9.11'
    }

    environment {
        MAVEN_DIR = tool(name: 'Maven 3.9.11', type: 'maven')
    }

//     environment {
//         MVN_CMD = "mvn"
// //     SONAR_HOST_URL = "http://<seu-sonar-host>:9000"
// //     SONAR_LOGIN = credentials('sonar-token-id')  // credencial armazenada no Jenkins
//     }


    stages {
//         stage('Set Maven Home') {
//             steps {
//                 script {
//                     if (isUnix()) {
//                         env.MAVEN_HOME = '/usr/share/maven/bin/'
//                     } else {
//                         env.MAVEN_HOME = '"C:\\Program Files\\JetBrains\\IntelliJ IDEA 2025.2.2\\plugins\\maven\\lib\\maven3\\bin\\"'
//                     }
//                     echo "MAVEN_HOME is set to: ${env.MAVEN_HOME}"
//                 }
//             }
//         }

        stage('Checkout') {
            steps {
                echo '📥 A fazer checkout do repositório...'
                checkout scm
            }
        }

//         stage('Checkout') {
//             steps {
//                 echo '📥 A fazer checkout do repositório...'
//                 git url: 'https://github.com/pfcosta02/mei-arqsoft-odsoft.git', branch: 'main'
//             }
//         }

        stage('Build & Compile') {
            steps {
                script {
                    echo 'Cleaning workspace...'
                    if (isUnix())
                    {
                        sh "mvn clean compile test-compile"
                    }
                    else
                    {
                        bat "mvn clean compile test-compile"
                    }
                }
            }
        }

        stage('Install') {
            steps {
                script {
                    echo 'Installing code...'
                    if (isUnix())
                    {
                        sh "mvn install -DskipTests"
                    }
                    else
                    {
                        bat "mvn install -DskipTests"
                    }
                }
            }
        }

// correr em parelelo os testes
// parallel {}

        stage('Unit Tests') {
            steps {
                script {
                    echo 'Running unit tests...'
                    if (isUnix())
                    {
                        sh "mvn surefire:test"
                    }
                    else
                    {
                        bat "mvn surefire:test"
                    }
                }
            }
        }
//
//         stage('Integration Tests') {
//             steps {
//                 script {
//                     echo 'Running integration tests...'
//                     if (isUnix())
//                     {
//                         sh "mvn failsafe:integration-test failsafe:verify"
//                     }
//                     else
//                     {
//                         bat "mvn failsafe:integration-test failsafe:verify"
//                     }
//                 }
//             }
//         }
//
        stage('Mutation Tests') {
            steps {
                script {
                    echo 'Running mutation tests...'
                    if (isUnix())
                    {
                        sh "mvn org.pitest:pitest-maven:mutationCoverage"
                    }
                    else
                    {
                        bat "mvn org.pitest:pitest-maven:mutationCoverage"
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv("sonarqube_local")
                    {
                        if (isUnix())
                        {
                            sh "mvn verify -X sonar:sonar"
                        }
                        else
                        {
                            bat "mvn verify -X sonar:sonar"
                        }
                    }
                }
            }
        }

                stage('Quality Gate') {
                    steps {
                        timeout(time: 3, unit: 'MINUTES') {
                            waitForQualityGate abortPipeline: true
                        }
                    }
                }



//         stage('SonarQube Static Code Analysis') {
//             steps {
//                 script {
//                     // Define o mapa de ambientes -> servidores SonarQube
//                     def ENVIRONMENT_2_SONARQUBE_SERVER = [
//                         'docker': 'sonarqube_docker',
//                         'local' : 'sonarqube_local'
//                     ]
//
//                     def sonarServer = ENVIRONMENT_2_SONARQUBE_SERVER[params.Environment]
//
//                     echo "Running SonarQube analysis using server: ${sonarServer}"
//
//                     withSonarQubeEnv(sonarServer)
//                     {
//                         if (isUnix())
//                         {
//                             sh "mvn verify -X org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.projectKey=odsoft_2025_1200909_1201270 -Dsonar.token=squ_0ac5031162d837cf1ed694409e5c3d5f15dce98d -Dsonar.coverage.jacoco.xmlReportPaths=target/jacoco/jacoco.xml"
//                         }
//                         else
//                         {
//                             bat "mvn verify -X sonar:sonar"
//                         }
//                     }
//                 }
//             }
//         }
//
//         stage('SonarQube Quality Gate') {
//             steps {
//                 timeout(time: 5, unit: 'MINUTES') {
//                     script {
//                         def qualityGateResult = waitForQualityGate(abortPipeline: true)
//                         if (qualityGateResult.status == 'OK')
//                         {
//                             echo 'Quality gate passed. Proceeding with the pipeline.'
//                         }
//                     }
//                 }
//             }
//         }
//
//
        stage('JaCoCo')
        {
            steps {
                jacoco execPattern: '**/target/jacoco.exec',
                       classPattern: '**/target/classes',
                       sourcePattern: '**/src/main/java',
                       inclusionPattern: '**/*.class'
            }
        }

        stage('Package') {
            steps {
                script {
                echo 'Building the final package...'
                if (isUnix())
                    {
                        sh 'mvn package -DskipTests'
                    }
                    else
                    {
                        bat 'mvn package -DskipTests'
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying the application...'
                // Adiciona comandos de deploy aqui
                // script
                // {
                //     sh '''
                //     scp target/myapp.jar user@dev-server:/opt/myapp/
                //     ssh user@dev-server "java -jar /opt/myapp/myapp.jar &"
                //     '''
                // }
            }
        }
    }

    post {
        success
        {
            echo 'Pipeline completed successfully.'
            // Meter aqui o html gerado pelo Pitest
            // archiveArtifacts artifacts: ""
            // junit skipPublishingChecks: true,  testResults:'**/target/surefire-reports/*.xml'
        }

        failure
        {
            echo 'Pipeline failure'
        }

        always
        {
            echo 'Performing cleanup...'
            // Cleanup code
        }
    }
}