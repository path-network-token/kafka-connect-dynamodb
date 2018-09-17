node {
    def scmVars = checkout scm

    def appName = 'kafka-dynamo-connect'
    def gitBranch = scmVars.GIT_BRANCH.replace('origin/', '')
    def gitCommitMsg = sh(script: 'git show -s --format=%B --oneline HEAD', returnStdout: true).trim()
    def packageVersion = "1.0.${BUILD_NUMBER}"
    def slackChannel = '#core-dev'
    def md5JobName = sh ( script: "echo '${gitBranch}${BUILD_NUMBER}' | md5sum - | head -c 6", returnStdout: true )
    def dockerProjectName = "kafka-dynamo-connect${md5JobName}"
    def dockerRegistry = 'https://153323634045.dkr.ecr.us-west-2.amazonaws.com'
    def ecrRegion = 'us-west-2'

    slackSend channel: "${slackChannel}", color: '#439FE0', message: "Starting ${env.JOB_NAME} - ${env.BUILD_NUMBER} - ${gitCommitMsg} (<${env.JOB_URL}|Open>)"
    currentBuild.displayName = "#${BUILD_NUMBER} - ${gitBranch}"

    try {
        if (gitBranch == 'master') {
            stage('Publish release') {
                println 'Building release Docker image...'
                sh "docker logout"
                docker.withRegistry(dockerRegistry) {
                    def image = docker.build("${appName}:${packageVersion}", "--build-arg APP_VERSION=${packageVersion} .")
                    try {
                        sh "aws ecr get-login --no-include-email --region ${ecrRegion} | bash"
                        image.push()
                        image.push('latest')
                    } finally {
                        sh "docker inspect ${image.imageName()} -f '{{.Id}}' | xargs docker rmi -f"
                        sh "docker image prune --force --filter label=stage=intermediate"
                    }
                }
            }
        }
    } catch (exc) {
        slackSend channel: "${slackChannel}", color: '#FF0000', message: "${env.STAGE_NAME} failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER} - ${gitBranch} (<${env.JOB_URL}|Open>)"
        currentBuild.result = 'FAILURE'
        throw exc
    } finally {
        println 'Finishing up...'
    }
    slackSend channel: "${slackChannel}", color: '#439FE0', message: "Finished ${env.JOB_NAME} - ${env.BUILD_NUMBER} - ${gitBranch} (<${env.JOB_URL}|Open>)"
}
