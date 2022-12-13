pipeline {
    environment {
        JAVA_TOOL_OPTIONS = "-Duser.home=/var/maven"
    }
    agent {
        docker {
            image "maven:3.8.6-openjdk-18"
            args '-u root -v /home/ci-cd/maven-repo:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2'
        }
    }

    stages {
        stage("Package") {
            steps {
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
                sh "docker-compose -f ./src/test/resources/compose-test.yml up -d"
                waitUntil{
                    sh 'timeout 120 wget --retry-connrefused --tries=120 --waitretry=1 -q http://localhost:8080/ -O /dev/null' 
                }
                sh "mvn failsafe:integration-test"
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}