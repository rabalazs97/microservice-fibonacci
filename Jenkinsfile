pipeline {
    environment {
        JAVA_TOOL_OPTIONS = "-Duser.home=/var/maven"
    }
    agent {
        dockerfile {
            args '-u root --net=host -v /home/ci-cd/maven-repo:/var/maven/.m2 -v /var/run/docker.sock:/var/run/docker.sock -e MAVEN_CONFIG=/var/maven/.m2'
        }
    }
    stages {
        stage("Build") {
            steps {
                sh "docker-compose -f compose-test.yml down"
                sh "mvn -version"
                sh "mvn clean package -DskipTests"
            }
        }
        stage("Unit Tests"){
            steps {
                sh "mvn test"
            }
        }
        stage("E2E Test"){
            steps{
                sh "docker-compose -version"
                sh "docker-compose -f compose-test.yml up -d"
                sleep(time:20,unit:"SECONDS")
                sh "mvn failsafe:integration-test"
                sh "docker-compose -f compose-test.yml down"
            }
        }
        stage("Build image and push to repo"){
            steps {
                sh "mvn spring-boot:build-image -DskipTests"
            }
        }
    }
    post {
        success {
            build job: 'deploy-fibonacci'
        }
        always {
            cleanWs()
        }
    }
}