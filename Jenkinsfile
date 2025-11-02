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
        APP_NAME = 'psoft-g1'
//         JAR_NAME = 'psoft-g1-0.0.1-SNAPSHOT.jar'

        // Portas para cada ambiente
        DEV_PORT = '8080'
        STAGING_PORT = '8081'
        PROD_PORT = '8082'

        // Paths para deploy local
        DEPLOY_BASE_PATH = '/opt/deployments'
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

//         stage('Unit Tests') {
//             steps {
//                 script {
//                     echo 'Running unit tests...'
//                     if (isUnix())
//                     {
//                         sh "mvn surefire:test"
//                     }
//                     else
//                     {
//                         bat "mvn surefire:test"
//                     }
//                 }
//             }
//             post {
//                 always {
//                     junit '**/target/surefire-reports/*.xml'
//                     publishHTML(target: [
//                         allowMissing: false,
//                         alwaysLinkToLastBuild: true,
//                         keepAll: true,
//                         reportDir: 'target/surefire-reports',
//                         reportFiles: 'index.html',
//                         reportName: 'Unit Tests Report'
//                     ])
//                 }
//             }
//         }
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

//         stage('Test Coverage') {
//             steps {
//                 script {
//                     echo '📊 Gerando relatório de cobertura...'
//                     if (isUnix()) {
//                         sh "mvn jacoco:report"
//                     } else {
//                         bat "mvn jacoco:report"
//                     }
//                 }
//             }
//             post {
//                 always {
//                     jacoco(
//                         execPattern: '**/target/jacoco.exec',
//                         classPattern: '**/target/classes',
//                         sourcePattern: '**/src/main/java',
//                         inclusionPattern: '**/*.class',
//                         minimumInstructionCoverage: '60',
//                         minimumBranchCoverage: '50'
//                     )
//                     publishHTML(target: [
//                         allowMissing: false,
//                         alwaysLinkToLastBuild: true,
//                         keepAll: true,
//                         reportDir: 'target/site/jacoco',
//                         reportFiles: 'index.html',
//                         reportName: 'JaCoCo Coverage Report'
//                     ])
//                 }
//             }
//         }


//         stage('Mutation Tests') {
//             steps {
//                 script {
//                     echo 'Running mutation tests...'
//                     if (isUnix())
//                     {
//                         sh "mvn org.pitest:pitest-maven:mutationCoverage"
//                     }
//                     else
//                     {
//                         bat "mvn org.pitest:pitest-maven:mutationCoverage"
//                     }
//                 }
//             }
//             post {
//                 always {
//                     publishHTML(target: [
//                         allowMissing: false,
//                         alwaysLinkToLastBuild: true,
//                         keepAll: true,
//                         reportDir: 'target/pit-reports',
//                         reportFiles: 'index.html',
//                         reportName: 'PIT Mutation Report'
//                     ])
//                 }
//             }
//
//         }

//         stage('SonarQube Analysis') {
//             steps {
//                 script {
//                     def ENVIRONMENT_2_SONARQUBE_SERVER = [
//                         'docker': 'sonarqube_docker',
//                         'local' : 'sonarqube_local'
//                     ]
//
//                     def sonarServer = ENVIRONMENT_2_SONARQUBE_SERVER[params.Environment]
//                     echo "Running SonarQube analysis using server: ${sonarServer}"
//                     withSonarQubeEnv(sonarServer)
//                     {
//                         if (isUnix())
//                         {
//                             sh "mvn verify -X sonar:sonar"
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
//         stage('Quality Gate') {
//             steps {
//                 timeout(time: 3, unit: 'MINUTES') {
//                     waitForQualityGate abortPipeline: true
//                 }
//             }
//         }

        stage('Package') {
                    steps {
                        script {
                            echo '📦 Building the final package...'
                            if (isUnix()) {
                                sh 'mvn package -DskipTests'
                                // Captura o nome do JAR
                                def jarPath = sh(script: "find target -name '*.jar' -type f | head -1", returnStdout: true).trim()
                                env.JAR_NAME = sh(script: "basename ${jarPath}", returnStdout: true).trim()
                            } else {
                                bat 'mvn package -DskipTests'
                                // Captura o nome do JAR no Windows

                                bat 'dir /b target\\*.jar > jarname.txt'
                                def jarName = readFile('jarname.txt').trim()
                                env.JAR_NAME = jarName

                            }
                            echo "📦 JAR gerado: ${env.JAR_NAME}"
                            currentBuild.displayName = "#${env.BUILD_NUMBER} - ${env.JAR_NAME}"

                        }
                    }
                    post {
                        success {
                            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                        }
                    }
                }

       stage('Deploy to DEV') {
           steps {
               script {
                   echo '🚀 Deploying to DEVELOPMENT environment...'
                   echo "📦 Using JAR: ${env.JAR_NAME}"
                   if (params.Environment == 'docker') {
                       deployDocker('dev', env.DEV_PORT)
                   } else {
                       deployLocal('dev', env.DEV_PORT)
                   }
               }
           }
       }

//                 stage('Smoke Test DEV') {
//                     steps {
//                         script {
//                             echo '💨 Running smoke tests on DEV...'
//                             smokeTest(env.DEV_PORT, 'dev')
//                         }
//                     }
//                 }

                stage('Deploy to STAGING') {
                    steps {
                        script {
                            echo '🚀 Deploying to STAGING environment...'
                            if (params.Environment == 'docker') {
                                deployDocker('staging', env.STAGING_PORT)
                            } else {
                                deployLocal('staging', env.STAGING_PORT)
                            }
                        }
                    }
                }

//                 stage('Smoke Test STAGING') {
//                     steps {
//                         script {
//                             echo '💨 Running smoke tests on STAGING...'
//                             smokeTest(env.STAGING_PORT, 'staging')
//                         }
//                     }
//                 }

                stage('Deploy to PRODUCTION') {
                    steps {
//                         input message: 'Deploy to PRODUCTION?', ok: 'Deploy'
                        script {
                            echo '🚀 Deploying to PRODUCTION environment...'
                            if (params.Environment == 'docker') {
                                deployDocker('production', env.PROD_PORT)
                            } else {
                                deployLocal('production', env.PROD_PORT)
                            }
                        }
                    }
                }

//                 stage('Smoke Test PRODUCTION') {
//                     steps {
//                         script {
//                             echo '💨 Running smoke tests on PRODUCTION...'
//                             smokeTest(env.PROD_PORT, 'production')
//                         }
//                     }
//                 }
            }

        post {
            success {
                echo '✅ Pipeline completed successfully!'
                script {
                    def deploymentSummary = """
                    ═══════════════════════════════════════════════════
                            DEPLOYMENT SUCCESSFUL
                    ═══════════════════════════════════════════════════
                    Build: #${env.BUILD_NUMBER}
                    Deployment Type: ${params.Environment}

                    Environments deployed:
                      🟢 DEV        → http://localhost:${env.DEV_PORT}
                      🟢 STAGING    → http://localhost:${env.STAGING_PORT}
                      🟢 PRODUCTION → http://localhost:${env.PROD_PORT}

                    Build URL: ${env.BUILD_URL}
                    ═══════════════════════════════════════════════════
                    """
                    echo deploymentSummary
                }
            }

        failure {
            echo '❌ Pipeline failed!'
            script {
                echo """
                ═══════════════════════════════════════════════════
                        PIPELINE FAILED
                ═══════════════════════════════════════════════════
                Build: #${env.BUILD_NUMBER}
                Check the logs at: ${env.BUILD_URL}console
                ═══════════════════════════════════════════════════
                """
            }
        }

        always
        {
            echo 'Performing cleanup...'
            // Cleanup code
        }
    }
}

// ═══════════════════════════════════════════════════
//              DEPLOYMENT FUNCTIONS
// ═══════════════════════════════════════════════════

def deployDocker(environment, port) {
    def imageName = "${env.APP_NAME}:${environment}"
    def containerName = "${env.APP_NAME}-${environment}"

    echo "🐳 Deploying ${environment} with Docker on port ${port}"

    if (isUnix()) {
        sh """
            # Remove container antigo se existir
            echo "Checking for existing container: ${containerName}"
            if docker ps -a --format '{{.Names}}' | grep -q "^${containerName}\$"; then
                echo "Stopping existing container..."
                docker stop ${containerName} || true
                echo "Removing existing container..."
                docker rm ${containerName} || true
            fi

            # Remove imagem antiga se existir
            if docker images --format '{{.Repository}}:{{.Tag}}' | grep -q "^${imageName}\$"; then
                echo "Removing old image..."
                docker rmi ${imageName} || true
            fi

            # Cria Dockerfile se não existir
            if [ ! -f Dockerfile ]; then
                echo "Creating Dockerfile..."
                cat > Dockerfile << 'EOF'
            FROM openjdk:17-jdk-slim
            WORKDIR /app
            COPY target/*.jar app.jar
            EXPOSE 8080
            ENV JAVA_OPTS=""
            ENTRYPOINT ["java", "-jar", "app.jar"]
            EOF
            fi

            # Build da nova imagem
            echo "Building Docker image: ${imageName}"
            docker build -t ${imageName} .

            # Inicia novo container
            echo "Starting new container: ${containerName}"
            docker run -d \\
                --name ${containerName} \\
                -p ${port}:8080 \\
                -e SPRING_PROFILES_ACTIVE=${environment} \\
                -e SERVER_PORT=8080 \\
                ${imageName}

            # Aguarda e verifica se o container está rodando
            echo "Waiting for container to start..."
            sleep 10

            if docker ps --format '{{.Names}}' | grep -q "^${containerName}\$"; then
                echo "✅ Container ${containerName} is running!"
                docker ps --filter "name=${containerName}" --format "table {{.Names}}\\t{{.Status}}\\t{{.Ports}}"
                echo ""
                echo "📋 Container logs (last 50 lines):"
                docker logs --tail 50 ${containerName}
            else
                echo "❌ Container failed to start or stopped!"
                echo "📋 Full container logs:"
                docker logs ${containerName}
                echo ""
                echo "💡 Container status:"
                docker ps -a --filter "name=${containerName}"
                exit 1
            fi
        """
    } else {
        bat """
            @echo off
            echo Checking for existing container: ${containerName}
            docker ps -a --format "{{.Names}}" | findstr /X "${containerName}" >nul 2>&1
            if %errorlevel% equ 0 (
                echo Stopping existing container...
                docker stop ${containerName} || echo Container already stopped
                echo Removing existing container...
                docker rm ${containerName} || echo Container already removed
            )

            echo Checking for existing image: ${imageName}
            docker images --format "{{.Repository}}:{{.Tag}}" | findstr /X "${imageName}" >nul 2>&1
            if %errorlevel% equ 0 (
                echo Removing old image...
                docker rmi ${imageName} || echo Image already removed
            )

            if not exist Dockerfile (
                echo Creating Dockerfile...
                (
                    echo FROM openjdk:17-jdk-slim
                    echo WORKDIR /app
                    echo COPY target/*.jar app.jar
                    echo EXPOSE 8080
                    echo ENV JAVA_OPTS=""
                    echo ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
                ) > Dockerfile
            )

            echo Building Docker image: ${imageName}
            docker build -t ${imageName} .

            echo Starting new container: ${containerName}
            docker run -d ^
                --name ${containerName} ^
                -p ${port}:8080 ^
                -e SPRING_PROFILES_ACTIVE=${environment} ^
                -e SERVER_PORT=8080 ^
                --restart unless-stopped ^
                ${imageName}

            timeout /t 5 /nobreak >nul
            docker ps --filter "name=${containerName}"
        """
    }
}

def deployLocal(environment, port) {
    echo "📁 Deploying ${environment} locally on port ${port}"

    if (isUnix()) {
        def deployPath = "${env.DEPLOY_BASE_PATH}/${environment}"
        sh """
            # Cria diretório se não existir
            mkdir -p ${deployPath}

            # Copia o JAR
            echo "Copying JAR ${env.JAR_NAME} to ${deployPath}..."
            cp target/${env.JAR_NAME} ${deployPath}/${env.JAR_NAME}

            # Para aplicação existente
            if [ -f ${deployPath}/app.pid ]; then
                OLD_PID=\$(cat ${deployPath}/app.pid)
                if ps -p \$OLD_PID > /dev/null 2>&1; then
                    echo "Stopping existing application (PID: \$OLD_PID)..."
                    kill \$OLD_PID || true
                    sleep 3
                    kill -9 \$OLD_PID 2>/dev/null || true
                fi
            fi

            # Mata qualquer processo na porta
            lsof -ti:${port} | xargs kill -9 2>/dev/null || true

            # Inicia nova aplicação
            echo "Starting application on port ${port}..."
            nohup java -jar ${deployPath}/${env.JAR_NAME} \\
                --server.port=${port} \\
                --spring.profiles.active=${environment} \\
                > ${deployPath}/app.log 2>&1 &

            NEW_PID=\$!
            echo \$NEW_PID > ${deployPath}/app.pid

            echo "✅ Application started with PID: \$NEW_PID"
            echo "Log file: ${deployPath}/app.log"

            # Aguarda um pouco para verificar se iniciou
            sleep 5
            if ps -p \$NEW_PID > /dev/null; then
                echo "✅ Application is running!"
            else
                echo "❌ Application failed to start!"
                cat ${deployPath}/app.log
                exit 1
            fi
        """
    } else {
        // Windows deployment
        def deployPath = "C:\\deployments\\${environment}"
        bat """
            @echo off
            if not exist "${deployPath}" mkdir "${deployPath}"

            echo JAR to deploy: ${env.JAR_NAME}
            echo Checking if JAR exists...
            if not exist "target\\${env.JAR_NAME}" (
                echo ERROR: JAR file not found: target\\${env.JAR_NAME}
                dir target\\*.jar
                exit /b 1
            )

            echo Copying JAR to ${deployPath}...
            copy /Y "target\\${env.JAR_NAME}" "${deployPath}\\${env.JAR_NAME}"
            if errorlevel 1 (
                echo ERROR: Failed to copy JAR file!
                exit /b 1
            )
            echo JAR copied successfully!

            echo Stopping existing application on port ${port}...
            for /f "tokens=5" %%a in ('netstat -aon ^| findstr :${port}') do taskkill /F /PID %%a 2^>NUL

            echo Waiting 2 seconds...
            ping 127.0.0.1 -n 3 > NUL

            echo Starting application on port ${port}...
            cd /d "${deployPath}"
            start "${env.APP_NAME}-${environment}" /MIN cmd /c "java -jar ${env.JAR_NAME} --server.port=${port} --spring.profiles.active=${environment} ^> app.log 2^>^&1"

            echo Waiting 5 seconds for application to start...
            ping 127.0.0.1 -n 6 > NUL
            echo Application started!
            echo Log file: ${deployPath}\\app.log
        """
    }
}

def smokeTest(port, environment) {
    def maxRetries = 30
    def retryDelay = 2

    echo "Running smoke test for ${environment} on port ${port}..."

    if (isUnix()) {
        sh """
            for i in \$(seq 1 ${maxRetries}); do
                echo "Attempt \$i/${maxRetries}: Testing http://localhost:${port}/actuator/health"

                if curl -f -s http://localhost:${port}/actuator/health > /dev/null 2>&1; then
                    echo "✅ Smoke test PASSED for ${environment}!"
                    curl -s http://localhost:${port}/actuator/health | head -n 20
                    exit 0
                fi

                echo "Service not ready yet, waiting ${retryDelay}s..."
                sleep ${retryDelay}
            done

            echo "❌ Smoke test FAILED for ${environment} after ${maxRetries} attempts!"
            exit 1
        """
    } else {
        bat """
            @echo off
            setlocal enabledelayedexpansion
            set count=0

            :retry
            set /a count+=1
            echo Attempt !count!/${maxRetries}: Testing http://localhost:${port}/actuator/health

            curl -f -s -o NUL http://localhost:${port}/actuator/health
            if !errorlevel! equ 0 (
                echo Smoke test PASSED for ${environment}!
                curl -s http://localhost:${port}/actuator/health
                exit /b 0
            )

            if !count! lss ${maxRetries} (
                echo Service not ready yet, waiting ${retryDelay}s...
                ping 127.0.0.1 -n 3 > NUL
                goto retry
            )
            
            echo Smoke test FAILED for ${environment}!
            exit /b 1
        """
    }
}