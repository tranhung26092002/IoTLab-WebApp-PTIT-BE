pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'eureka-service:latest'
        CONTAINER_NAME = 'eureka-service-container'
    }

    stages {
        stage('Clean Old Containers') {
            steps {
                script {
                    // Kiểm tra và xóa container cũ nếu tồn tại
                    echo 'Checking for existing containers...'
                    if (dockerInspect(CONTAINER_NAME)) {
                        echo 'Stopping and removing old container...'
                        sh "docker stop ${CONTAINER_NAME} || true"
                        sh "docker rm ${CONTAINER_NAME} || true"
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image từ Dockerfile
                    echo 'Building Docker image...'
                    sh "docker build -t ${DOCKER_IMAGE} ."
                }
            }
        }

        stage('Create New Container') {
            steps {
                script {
                    // Tạo container mới từ image vừa build
                    echo 'Creating new container...'
                    sh "docker run -d --name ${CONTAINER_NAME} ${DOCKER_IMAGE}"
                }
            }
        }
    }

    post {
        always {
            echo 'Pipeline finished.'
        }
    }
}

// Hàm kiểm tra container có tồn tại hay không
def dockerInspect(containerName) {
    return sh(script: "docker inspect --format='{{.State.Running}}' ${containerName}", returnStatus: true) == 0
}
