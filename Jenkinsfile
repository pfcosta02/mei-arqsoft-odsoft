pipeline {
    agent any

    triggers {
        githubPush()
    }

    tools {
        maven 'Maven 3.9.11'
    }

    parameters {
        choice(
            name: 'Environment',
            choices: ["docker", "local"],
            description: 'Choose an Environment.'
        )
    }

    environment {
        MAVEN_DIR = tool(name: 'Maven 3.9.11', type: 'maven')
    }

    stages {
        stage('Checkout') {
            steps {
                echo '📥 A fazer checkout do repositório...'
                checkout scm
            }
        }

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
                    def ENVIRONMENT_2_SONARQUBE_SERVER = [
                        'docker': 'sonarqube_docker',
                        'local' : 'sonarqube_local'
                    ]

                    def sonarServer = ENVIRONMENT_2_SONARQUBE_SERVER[params.Environment]
                    echo "Running SonarQube analysis using server: ${sonarServer}"
                    withSonarQubeEnv(sonarServer)
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