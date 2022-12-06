pipeline {
    environment {
        JAVA_TOOL_OPTIONS = "-Duser.home=/var/maven"
    }
    agent {
        docker {
            image "maven:3.8.3-openjdk-17"
            args '-v /home/ci-cd/maven-repo:/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2'
        }
    }

    stages {
        stage("Build") {
            steps {
                sh "mvn -version"
                sh "mvn clean install"
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}