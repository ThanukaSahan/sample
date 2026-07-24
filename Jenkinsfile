pipeline {
    agent any

    environment {
        APP_NAME = 'spring-boot-app'
        IMAGE_TAG = "${BUILD_NUMBER}"
        POSTMAN_ENV = 'postman/environment.json'
        POSTMAN_COLL = 'postman/spring-app-tests.json'
    }

    stages {
        stage('1. Checkout Code') {
            steps {
                git branch: 'main', url: 'https://github.com/your-user/your-repo.git'
            }
        }

        stage('2. Build & Unit Tests') {
            steps {
                echo 'Building Spring Boot JAR and executing unit tests...'
                sh './mvnw clean test'
            }
        }

        stage('3. SonarQube Analysis') {
            steps {
                // Requires "SonarQube Scanner" plugin installed in Jenkins
                withSonarQubeEnv('SonarQube') {
                    sh './mvnw sonar:sonar \
                        -Dsonar.projectKey=spring-boot-app \
                        -Dsonar.java.binaries=target/classes'
                }
            }
        }

        stage('4. Build Docker Image') {
            steps {
                echo 'Building Docker container image...'
                sh "docker build -t ${APP_NAME}:${IMAGE_TAG} ."
                sh "docker tag ${APP_NAME}:${IMAGE_TAG} ${APP_NAME}:latest"
            }
        }

        stage('5. Deploy Container for Testing') {
            steps {
                echo 'Deploying application container...'
                // Stop & remove existing container if running
                sh 'docker stop spring-app-test || true'
                sh 'docker rm spring-app-test || true'
                // Run new container on port 8080
                sh "docker run -d --name spring-app-test -p 8080:8080 ${APP_NAME}:${IMAGE_TAG}"
                
                // Give Spring Boot a few seconds to boot up completely
                sleep 15
            }
        }

        stage('6. Run Postman Integration Tests') {
            steps {
                echo 'Executing API Tests via Postman (Newman)...'
                // Uses the official Postman Newman Docker image to run tests against the app
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
            echo 'Cleaning up old images...'
            sh 'docker image prune -f'
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed. Please inspect logs.'
        }
    }
}
