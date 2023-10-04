pipeline {
    agent {label 'inbestment-agent'}
    stages {
        stage('Pull') {
            steps {
                // sh 'echo "Pull your application code"'
                // git 'https://github.com/atulyw/ecom-service.git'
                git credentialsId: 'github', url: 'git@github.com:atulyw/ecom-service.git'
            }
        }
        stage('Build') {
            steps {
                // sh 'echo "Build your application"'
                sh '''
                curl -O https://dlcdn.apache.org/maven/maven-3/3.9.4/binaries/apache-maven-3.9.4-bin.tar.gz
                tar -xvf apache-maven-3.9.4-bin.tar.gz -C /opt
                export PATH=$PATH:/opt/apache-maven-3.9.4/bin
                mvn clean package -DskipTests
                '''
            }
        }
        stage('Release') {
            steps {
                sh 'echo "Release your application"'
            }
        }
    }
}




1. ECS/farget plugin.
2. Create IAM role  -- ec2 + ECS + ECS-task admin/ecs only & attach to jenkins instance.
    (this will also be used as an replacement for password in jenkins cloud)
            {
                "Version": "2012-10-17",
                "Statement": [
                    {
                        "Effect": "Allow",
                        "Principal": {
                            "Service": [
                                "ec2.amazonaws.com",
                                "ecs.amazonaws.com",
                                "ecs-tasks.amazonaws.com"
                            ]
                        },
                        "Action": "sts:AssumeRole"
                    }
                ]
            }
3. manage jenkins > security > add fixed port you want (eg:- 5001)
4. aws > ec2 > jenkins server SG > add tcp port 5001 in inbound rules so that it can access that port.
5. jenkins server > manage jenkins > cloud > create cloud.
- no credentials
- select region
- Tunnel connection through = 3.83.86.125:5001
- Alternative Jenkins URL   = http://3.83.86.125:8080/
- ecs agent template add.
  - label     = name of cloud given Eg: inbestment-agent (label is used to target the source cloud)
  - template  = inbestment-agent-template
  - launchType= farget.
  - Operating System Family = linux
  - cpu architure = x86_64
  - add subnets
  - security groups = create a security group with inbound 8080, outbound 0.0.0.0/0 & paste id.
  - Assign Public Ip = yes
  - task role arn           = arn of role create in step 2.  (apply over instance)
  - task execution role arn = arn of role create in step 2.  (apply inside instance during execution)
  - save

6. create docker file & Push to ECR:  (this is to solve Sudo error problem)
- create docker:
  FROM jenkins/inbound-agent
  USER root
  
- create Repository in ECR
- view ECR push commands to build & push docker image to ECR.
- Copy ECR image URL (E.g: public.ecr.aws/t6w8h6d3/ujweshjenkinsimage:latest) & paste it into: manage jenkins > cloud > configure > Advance > Docker Image > public.ecr.aws/t6w8h6d3/ujweshjenkinsimage:latest

7. For using SSH authentication and not Https:-
- manage jenkins > security > go to last step > Git Host Key Verification Configuration > select No Verification.
- this will not give error when you add SSH private key in Dashboard > pipeline > Pipeline Syntax via SSH private key.

8. Add private key genreated from Ssh-keygen to jenkins for authentication:
- Dashboard > pipeline > Pipeline Syntax > select git:Git
- add SSH repo url: git@github.com:atulyw/ecom-service.git
- create credentials: kind > SSH username with private keypair. > ID = github (can give anything)
- select this credentials generated.
- genreated pipeline Syntax.
- copy this and paste it in git pull / SCM pull stage.
- copy this pipeline and paste it in Dashboard > ecs-demo-pipeline > configure > Script.
- Save.


9. Trigger the pipeline