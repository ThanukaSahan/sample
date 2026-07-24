pipeline {
    agent any

    environment {
        APP_NAME = 'spring-boot-app'
        IMAGE_TAG = "${BUILD_NUMBER}"
    }

    tools {
        maven 'Maven3' // Ensures 'mvn' command is available across all stages
    }

    stages {
        stage('1. Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('2. Build & Unit Tests') {
            steps {
                echo 'Building Spring Boot JAR and running tests...'
                sh 'mvn clean test'
            }
        }

        stage('3. SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar \
                        -Dsonar.projectKey=spring-boot-app \
                        -Dsonar.java.binaries=target/classes'
                }
            }
        }

        stage('4. Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                sh "docker build -t ${APP_NAME}:${IMAGE_TAG} ."
                sh "docker tag ${APP_NAME}:${IMAGE_TAG} ${APP_NAME}:latest"
            }
        }

        stage('5. Deploy Container for Testing') {
            steps {
                echo 'Deploying application...'
                sh 'docker stop spring-app-test || true'
                sh 'docker rm spring-app-test || true'
                sh "docker run -d --name spring-app-test -p 8080:8080 ${APP_NAME}:${IMAGE_TAG}"
                sleep 15
            }
        }

        stage('6. Run Postman Integration Tests') {
            steps {
                echo 'Executing Postman tests with Newman...'
                sh """
                  docker run --rm \
                  --network="host" \
                  -v \$(pwd)/postman:/etc/newman \
                  postman/newman \
                  run /etc/newman/spring-app-tests.json \
                  -e /etc/newman/environment.json
                """
            }
        }
    }

    post {
        always {
            echo 'Cleaning up dangling images...'
            sh 'docker image prune -f || true'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs above.'
        }
    }
}