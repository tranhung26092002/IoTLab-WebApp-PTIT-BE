pipeline {
    agent any
    
    tools {
        maven 'Maven'
        dockerTool 'Docker'
    }

    environment {
        DOCKER_IMAGE = 'eureka-service:latest'
        CONTAINER_NAME = 'eureka-service'
    }

    stages {
        stage('Check Tools') {
            steps {
                sh '''
                    docker --version
                    mvn --version
                    docker-compose --version
                '''
            }
        }

        stage('Maven Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Clean Old Containers') {
            steps {
                script {
                    echo 'Checking for existing containers...'
                    sh '''
                        if docker ps -q -f name=$CONTAINER_NAME | grep -q .; then
                            echo "Stopping containers..."
                            docker-compose down || true
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
                    sh 'docker-compose build --no-cache'
                    sh 'docker-compose up -d'
                    sh 'docker ps | grep $CONTAINER_NAME'
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                sh 'sleep 30'
                sh 'curl -f http://localhost:8761 || exit 1'
            }
        }
    }

    post {
        always {
            sh 'docker system prune -f || true'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
            sh 'docker-compose logs'
        }
    }
}