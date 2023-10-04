pipeline {
    agent any
    stages {
        stage('Pull') {
            steps {
                sh 'echo "Pull your application code"'
            }
        }
        stage('Build') {
            steps {
                sh 'echo "Build your application"'
            }
        }
        stage('Test') {
            steps {
                sh 'echo "Test your application"'
            }
        }
    }
}
