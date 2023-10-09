pipeline {
    agent any
    environment {
        AWS_DEFAULT_REGION = "ap-south-1"
        CLUSTER_NAME= "WhiteBooks-Prod-ECS-Cluster-Prod"
        DESIRED_COUNT= "1"
        BRANCH_NAME = "${GIT_BRANCH.split('origin/')[1].replaceAll('/','-')}"
        //DOCKER_IMAGE_TAG = "${BRANCH_NAME}_${GIT_COMMIT.take(7)}"
        ECR_REPO_URL = "473620331716.dkr.ecr.ap-south-1.amazonaws.com/ihub-demo-1"
        SERVICE_NAME = "iHUB-Demo"
        FAMILY = "iHUB-Demo-1"
    }
    stages {
       stage('Checkout GitHub') {
            steps {
             sh '''
             env
              '''
             git branch: 'main', url: 'https://github.com/kishorkumbhar/iHUB.git'
            }
        }
        stage('Setting Up Docker Tag , Build and Push Image to ECR'){
            steps{
                script {
                    sh returnStdout: true, script: 'aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $ECR_REPO_URL'
                    GIT_COMMIT_ID = sh (
                                    script: 'git rev-parse --short HEAD',
                                    returnStdout: true
                                  ).trim()
                TIMESTAMP = sh (
                                script: 'date +%Y%m%d%H%M',
                                returnStdout: true
                              ).trim()

                    env.IMAGETAG= "${GIT_COMMIT_ID}-${TIMESTAMP}"
                    echo "image tag is ${IMAGETAG}"
                    currentBuild.displayName = "[${BUILD_NUMBER}] ${IMAGETAG}"
                    currentBuild.description = "SERVICE_NAME:${SERVICE_NAME}"
                    sh "mvn clean install"
                    sh "docker build -t ${ECR_REPO_URL}:${IMAGETAG} -f Dockerfile ."
                    sh "docker push ${ECR_REPO_URL}:${IMAGETAG}"
                   }
                }
            }
        stage('deploy to ecs'){
               steps {
                 withEnv(["IMAGETAG=${IMAGETAG}"]){
                 sh """
                    sed -i -e 's|ECR_REPO_URL|${ECR_REPO_URL}|' taskdef.json
                    sed -i -e 's|DOCKER_IMAGE_TAG|${IMAGETAG}|' taskdef.json
                 """
                sh '''
                    FAMILY=`cat taskdef.json | jq -r .family`
                    #FAMILY=`sed -n \'s/.*"family": "\\(.*\\)",/\\1/p\' taskdef.json`
                    aws ecs register-task-definition --family ${FAMILY} --cli-input-json file://taskdef.json --region ${AWS_DEFAULT_REGION}
                    REVISION=`aws ecs describe-task-definition --task-definition ${FAMILY} --region ${AWS_DEFAULT_REGION} | jq -r .taskDefinition.revision`
                    echo "REVISION:${REVISION}"
                    aws ecs update-service --cluster ${CLUSTER_NAME} --region ${AWS_DEFAULT_REGION} --service ${SERVICE_NAME} --task-definition ${FAMILY}:${REVISION} --desired-count ${DESIRED_COUNT}
                '''
                 }
               }
            }
    }
    post {
          always {
            cleanWs()
                        }
    }
}
