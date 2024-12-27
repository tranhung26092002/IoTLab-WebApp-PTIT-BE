pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'eureka-service:latest'
        CONTAINER_NAME = 'eureka-service'
    }

    stages {
        stage('Maven Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Check Docker Compose') {
            steps {
                sh 'docker-compose --version'
            }
        }

        stage('Clean Old Containers') {
            steps {
                script {
                    echo 'Checking for existing containers...'
                    sh '''
                        if docker ps -q -f name=$CONTAINER_NAME | grep -q .; then
                            echo "Stopping containers..."
                            docker-compose down
                        else
                            echo "No containers found"
                        fi
                    '''
                }
            }
        }

        stage('Build and Deploy') {
            steps {
                script {
                    sh 'docker-compose build'
                    sh 'docker-compose up -d'
                }
            }
        }
    }

    post {
        always {
            sh 'docker system prune -f'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
