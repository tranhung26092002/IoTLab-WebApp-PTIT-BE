pipeline {
    agent any
    
    environment {
        DOCKER_REGISTRY = 'tranvanhung26092002'
        DOCKER_CREDENTIALS = credentials('docker-hub-credentials')
        SERVICES = ['api-gateway', 'user-service', 'device-service', 'practice-service', 'storage-service']
        MAVEN_OPTS = '-Dmaven.repo.local=/tmp/maven-repo'
        JAVA_HOME = tool 'JDK17'
    }
    
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        
        stage('Code Quality Check') {
            steps {
                script {
                    SERVICES.each { service ->
                        dir(service) {
                            echo "Running code quality checks for ${service}..."
                            sh "mvn sonar:sonar -Dsonar.projectKey=${service}"
                        }
                    }
                }
            }
        }
        
        stage('Build All Services') {
            steps {
                script {
                    SERVICES.each { service ->
                        dir(service) {
                            echo "Building ${service}..."
                            sh "mvn clean package -DskipTests"
                        }
                    }
                }
            }
        }
        
        stage('Test All Services') {
            steps {
                script {
                    SERVICES.each { service ->
                        dir(service) {
                            echo "Running tests for ${service}..."
                            sh "mvn test"
                        }
                    }
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    SERVICES.each { service ->
                        dir(service) {
                            echo "Building Docker image for ${service}..."
                            sh "docker build -t ${DOCKER_REGISTRY}/${service}:${BUILD_NUMBER} ."
                            sh "docker tag ${DOCKER_REGISTRY}/${service}:${BUILD_NUMBER} ${DOCKER_REGISTRY}/${service}:latest"
                        }
                    }
                }
            }
        }
        
        stage('Push Docker Images') {
            steps {
                script {
                    sh '''
                        echo "Logging in to Docker Hub..."
                        echo ${DOCKER_CREDENTIALS_PSW} | docker login -u ${DOCKER_CREDENTIALS_USR} --password-stdin
                    '''
                    
                    SERVICES.each { service ->
                        sh "docker push ${DOCKER_REGISTRY}/${service}:${BUILD_NUMBER}"
                        sh "docker push ${DOCKER_REGISTRY}/${service}:latest"
                    }
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main') {
                        echo "Deploying all services to production..."
                        sh '''
                            # Pull latest images
                            docker-compose pull
                            
                            # Deploy using docker-compose
                            docker-compose up -d
                            
                            # Health check
                            sleep 30
                            docker-compose ps
                        '''
                    } else {
                        echo "Skipping deployment for non-main branch"
                    }
                }
            }
        }
    }
    
    post {
        always {
            sh '''
                echo "Cleaning up..."
                docker system prune -f
            '''
        }
        success {
            echo 'Pipeline completed successfully!'
            emailext (
                subject: "Pipeline Successful: ${currentBuild.fullDisplayName}",
                body: "Pipeline ${currentBuild.fullDisplayName} completed successfully!",
                to: '${DEFAULT_RECIPIENTS}'
            )
        }
        failure {
            echo 'Pipeline failed!'
            emailext (
                subject: "Pipeline Failed: ${currentBuild.fullDisplayName}",
                body: "Pipeline ${currentBuild.fullDisplayName} failed! Please check the console output for more details.",
                to: '${DEFAULT_RECIPIENTS}'
            )
        }
    }
} 