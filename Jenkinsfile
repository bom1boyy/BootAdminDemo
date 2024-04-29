pipeline {
    agent any
    parameters {
        string(name: 'DEPLOYMENT_NAME', defaultValue: 'test-deployment', description: 'K8s deployment name')
        string(name: 'APPLICATION_NAME', defaultValue: 'test-app', description: 'K8s deployment application name')
        string(name: 'CONTAINER_NAME', defaultValue: 'test-container', description: 'K8s deployment container name')
        string(name: 'SERVICE_NAME', defaultValue: 'test-service', description: 'K8s service name')
        string(name: 'IMAGE_NAME', defaultValue: 'test', description: 'Docker image name')
        string(name: 'IMAGE_TAG', defaultValue: '1.0', description: 'Docker image tag')
        string(name: 'REGISTRY_URL', defaultValue: '192.168.6.104:5000', description: 'Private Docker registry URL')
        string(name: 'CONTAINER_PORT', defaultValue: '8080', description: 'Container port')
        string(name: 'SERVICE_PORT', defaultValue: '80', description: 'Service port')
        string(name: 'TARGET_PORT', defaultValue: '8080', description: 'Target port')
        string(name: 'REPLICAS', defaultValue: '3', description: 'Deployment replicas ea')
    }
    stages {
        stage('Clone repository') {
            agent any
            steps {
                echo '**********************************************************'
                echo '****************** Clone repository ... ******************'
                echo '**********************************************************'
                checkout scm
            }
            post {
                failure{
                    error "Fail Clone Repository"
                }
            }
        }
        stage('Build') {
            agent any
            steps{
                echo '**********************************************************'
                echo '******************** Build start ... *********************'
                echo '**********************************************************'
                sh './gradlew clean build'
            }
            post{
                failure{
                    error 'Fail Build'
                }
            }
        }
        stage('Build docker image') {
            agent any
            steps{
                echo '**********************************************************'
                echo '***************** Image Build start ... ******************'
                echo '**********************************************************'
                dir('./') {
                    sh "docker build --no-cache -t ${params.IMAGE_NAME}:${params.IMAGE_TAG} -f Dockerfile ."
                }
            }
            post{
                failure{
                    error 'Fail Image build'
                }
            }
        }
        stage('Push to Private Registry') {
            agent any
            steps{
                echo '**********************************************************'
                echo '********* Pushing image to private registry ... **********'
                echo '**********************************************************'
                sh "docker tag ${params.IMAGE_NAME}:${params.IMAGE_TAG} ${params.REGISTRY_URL}/${params.IMAGE_NAME}:${params.IMAGE_TAG}"
                sh "docker push ${params.REGISTRY_URL}/${params.IMAGE_NAME}:${params.IMAGE_TAG}"
                sh "docker image rmi ${params.REGISTRY_URL}/${params.IMAGE_NAME}:${params.IMAGE_TAG}"
                sh "docker image rm ${params.IMAGE_NAME}:${params.IMAGE_TAG}"
            }
            post{
                failure{
                    error 'Fail Push to Private Registry'
                }
            }
        }
        stage('Create YAML files') {
            agent any
            steps {
                echo '**********************************************************'
                echo '******* Creating deployment and service YAML files *******'
                echo '**********************************************************'
// deployment.yaml 파일 생성
writeFile file: 'deployment.yaml', text: """
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${params.DEPLOYMENT_NAME}
spec:
  replicas: ${params.REPLICAS}
  selector:
    matchLabels:
      app: ${params.APPLICATION_NAME}
  template:
    metadata:
      labels:
        app: ${params.APPLICATION_NAME}
    spec:
      containers:
      - name: ${params.CONTAINER_NAME}
        image: ${params.REGISTRY_URL}/${params.IMAGE_NAME}:${params.IMAGE_TAG}
        ports:
        - containerPort: ${params.CONTAINER_PORT}
"""
// service.yaml 파일 생성
writeFile file: 'service.yaml', text: """
apiVersion: v1
kind: Service
metadata:
  name: ${params.SERVICE_NAME}
spec:
  selector:
    app: ${params.APPLICATION_NAME}
  ports:
    - protocol: TCP
      port: ${params.SERVICE_PORT}
      targetPort: ${params.TARGET_PORT}
  type: NodePort
"""
            }
            post {
                always {
                    archiveArtifacts artifacts: '*.yaml'
                }
            }
        }
        stage('Deploy on k8s cluster') {
            agent any
            steps{
                echo '**********************************************************'
                echo '********** Deploy resources on k8s cluster ... ***********'
                echo '**********************************************************'
                sh 'cat deployment.yaml'
                    sshPublisher(
                        failOnError: true,
                        publishers: [
                            sshPublisherDesc(
                                configName: '6.101(K8S-MASTER)',
                                verbose: true,
                                transfers: [
                                    sshTransfer(
                                        cleanRemote:false,
                                        // removePrefix: '.',
                                        sourceFiles: 'deployment.yaml',
                                        remoteDirectory: '/opt/jenkins-test/',
                                    ),
                                    sshTransfer(
                                        execCommand: 'kubectl apply -f /root/opt/jenkins-test/deployment.yaml'
                                    )
                                ]
                            )
                        ]
                    )
                sh 'cat service.yaml'
                    sshPublisher(
                        failOnError: true,
                        publishers: [
                            sshPublisherDesc(
                                configName: '6.101(K8S-MASTER)',
                                verbose: true,
                                transfers: [
                                    sshTransfer(
                                        cleanRemote:false,
                                        // removePrefix: '.',
                                        sourceFiles: 'service.yaml',
                                        remoteDirectory: '/opt/jenkins-test/',
                                    ),
                                    sshTransfer(
                                        execCommand: 'kubectl apply -f /root/opt/jenkins-test/service.yaml'
                                    )
                                ]
                            )
                        ]
                    )
            }
            post{
                failure{
                    error 'Fail Deploy resources on K8s cluster'
                }
            }
        }
    }
}
