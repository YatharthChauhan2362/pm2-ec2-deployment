pipeline {
    agent any

    stages {
        stage("Code") {
            steps {
                echo "Cloning the code"
                git url: "https://gitlab.webelight.co.in/webelight/devops/training.git", branch: "yatharth-jenkins", credentialsId: "gitup"
            }
        }
    
        stage("Build") {
            steps {
                echo "Building the code"
                sh "docker build -t yc-app-jenkins ."
            }
        }
        
        stage("Push to Docker Hub") {
            steps {
                echo "Pushing the image to Docker Hub"
                withCredentials([usernamePassword(credentialsId: "dockerhub", passwordVariable: "dockerHubPass", usernameVariable: "dockerHubUser")]) {
                sh "docker tag yc-app-jenkins ${env.dockerHubUser}/yc-app-jenkins:latest"
                sh "docker login -u ${env.dockerHubUser} -p ${env.dockerHubPass}"
                sh "docker push ${env.dockerHubUser}/yc-app-jenkins:latest"
                }
            }
        }
        
        stage("Run Docker Container") {
            steps {
                echo "Running the Docker container"
                // sh "docker-compose down"
                // sh "docker-compose up -d"
                sh "docker run -d -p 3000:3000 yatharthchauhan2024/yc-app-jenkins:latest"
                
            }
            
        }
        
    }
}
