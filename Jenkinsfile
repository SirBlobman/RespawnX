pipeline {
    agent any

    options {
        githubProjectProperty(projectUrlStr: "https://github.com/SirBlobman/RespawnX")
    }

    environment {
        DISCORD_URL = credentials('PUBLIC_DISCORD_WEBHOOK')
    }

    triggers {
        githubPush()
    }

    tools {
        jdk "JDK 17"
    }

    stages {
        stage("Gradle: Build") {
            steps {
                withGradle {
                    sh("./gradlew --refresh-dependencies --no-daemon clean build")
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'build/libs/RespawnX-*.jar', fingerprint: true
        }

        always {
            script {
                discordSend webhookURL: DISCORD_URL, title: "RespawnX", link: "${env.BUILD_URL}",
                        result: currentBuild.currentResult,
                        description: """\
                        **Branch:** ${env.GIT_BRANCH}
                        **Build:** ${env.BUILD_NUMBER}
                        **Status:** ${currentBuild.currentResult}""".stripIndent(),
                        enableArtifactsList: false, showChangeset: true
            }
        }
    }
}
