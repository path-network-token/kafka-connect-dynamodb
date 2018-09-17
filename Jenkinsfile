node {
    def scmVars = checkout scm

    def appName = 'kafka-dynamo-connect'
    def gitBranch = scmVars.GIT_BRANCH.replace('origin/', '')
    def gitCommitMsg = sh(script: 'git show -s --format=%B --oneline HEAD', returnStdout: true).trim()
    def packageVersion = "1.0.${BUILD_NUMBER}"
    def slackChannel = '#core-dev'
    def md5JobName = sh ( script: "echo '${gitBranch}${BUILD_NUMBER}' | md5sum - | head -c 6", returnStdout: true )
    def dockerProjectName = "kafka-dynamo-connect${md5JobName}"

    slackSend channel: "${slackChannel}", color: '#439FE0', message: "Starting ${env.JOB_NAME} - ${env.BUILD_NUMBER} - ${gitCommitMsg} (<${env.JOB_URL}|Open>)"
    currentBuild.displayName = "#${BUILD_NUMBER} - ${gitBranch}"

    try {
        if (gitBranch == 'master') {
            stage('Publish release') {
                println 'Building release Docker image...'
                  def image = docker.build("pathnetwork/${appName}:${packageVersion}",
                                           "--build-arg APP_VERSION=${packageVersion} .")
                  try {
                      withCredentials([usernamePassword(credentialsId: 'docker-hub-credentials', passwordVariable: 'DOCKERHUB_PASSWORD', usernameVariable: 'DOCKERHUB_USERNAME')]) {
                        sh "docker login -u ${DOCKERHUB_USERNAME} -p ${DOCKERHUB_PASSWORD}"
                      }
                      image.push()
                      image.push('latest')
                      slackSend channel: "${slackChannel}", color: '#439FE0', message: "Docker image pushed to Docker Hub ${env.JOB_NAME} - ${env.BUILD_NUMBER} - ${gitBranch} (<${env.JOB_URL}|Open>)"
                  } finally {
                      sh "docker inspect ${image.imageName()} -f '{{.Id}}' | xargs docker rmi -f"
                      sh "docker image prune --force --filter label=stage=intermediate"
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
