pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'user-service:latest'
        CONTAINER_NAME = 'user-service'
        JWT_KEY = credentials('JWT_KEY')
        REFRESH_TOKEN_KEY = credentials('REFRESH_TOKEN_KEY')
        POSTGRESQL_USERNAME = credentials('POSTGRESQL_USERNAME')
        POSTGRESQL_PASSWORD = credentials('POSTGRESQL_PASSWORD')
        BREVO_API_KEY = credentials('BREVO_API_KEY')
    }

    stages {
        stage('Clean Old Containers') {
            steps {
                script {
                    echo 'Stopping and removing old containers...'
                    sh '''
                        docker-compose down || true
                        docker container prune -f || true
                    '''
                }
            }
        }

        stage('Fix Permissions') {
            steps {
                script {
                    echo 'Fixing permissions for mvnw...'
                    sh 'chmod +x ./mvnw'
                }
            }
        }

        stage('Build Application') {
            steps {
                script {
                    echo 'Building application...'
                    sh './mvnw clean package -DskipTests'
                }
            }
        }

        stage('Verify Jar Existence') {
            steps {
                script {
                    echo 'Checking if user-service.jar exists...'
                    sh '''
                    if [ ! -f target/user-service.jar ]; then
                        echo "user-service.jar not found! Exiting..."
                        exit 1
                    fi
                    '''
                }
            }
        }

        stage('Build and Deploy with Docker') {
            steps {
                script {
                    echo 'Building and deploying Docker containers...'
                    sh '''
                    docker-compose down || true
                    docker-compose up -d --build
                    '''
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up workspace and Docker resources...'
            sh 'docker system prune -f'
            cleanWs()
        }
        success {
            echo 'Pipeline executed successfully!'
        }
        failure {
            echo 'Pipeline failed.'
        }
    }
}
