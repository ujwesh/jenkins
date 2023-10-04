pipeline {
    agent {
        docker { image 'node:18.18.0-alpine3.18' }
    }
    stages {
        stage('Test') {
            steps {
                sh 'node --version'
            }
        }
    }
}


// NOTE:
// 1. install docker pipeline plugin
// 2. add jenkins user (typically jenkins) docker group:
//     - sudo usermod -aG docker jenkins
// 3. restart jenkins.
// 4. check all users added in docker group:
//     - cat /etc/group  OR - getent group docker

