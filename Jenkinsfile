pipeline {
    environment {
        JAVA_TOOL_OPTIONS = "-Duser.home=/var/maven"
    }
    agent {
        dockerfile {
            args '-u root -v /home/ci-cd/maven-repo:/var/maven/.m2 -v /var/run/docker.sock:/var/run/docker.sock -e MAVEN_CONFIG=/var/maven/.m2'
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
                sh "docker-compose -version"
                sh "docker-compose -f compose-test.yml up -d"
                waitUntil{
                    sh 'timeout 120 wget --retry-connrefused --tries=120 --waitretry=1 -q http://localhost:7777/ -O /dev/null' 
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