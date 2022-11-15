/*
 *   Copyright 2022  SenX S.A.S.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
import hudson.model.*
@Library('senx-shared-library') _

pipeline {
  agent any
  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '3'))
  }

  environment {
    GPG_KEY_NAME = "${env.gpgKeyName}"
    NEXUS_HOST = "${env.nexusHost}"
    NEXUS_CREDS = credentials('nexus')
    OSSRH_CREDS = credentials('ossrh')
    GRADLE_CMD = './gradlew \
                -Psigning.gnupg.keyName=$GPG_KEY_NAME \
                -PossrhUsername=$OSSRH_CREDS_USR \
                -PossrhPassword=$OSSRH_CREDS_PSW \
                -PnexusHost=$NEXUS_HOST \
                -PnexusUsername=$NEXUS_CREDS_USR \
                -PnexusPassword=$NEXUS_CREDS_PSW'
  }
  stages {
    stage('Checkout') {
      steps {
        script {
          env.version = ""
          notify.slack('STARTED')
        }
        git poll: false, branch: 'main', url: 'git@gitlab.com:senx/WarpFleet-gradle-plugin.git'
        sh 'git checkout main'
        sh 'git fetch --tags'
        sh 'git pull origin main'

        script {
          env.version = gitUtils.getVersion()
        }
      }
    }

    stage('Build') {
      steps {
        sh "(cd ./buildSrc && ${GRADLE_CMD} clean build)"
      }
    }

    stage('Package') {
      steps {
        sh "(cd ./buildSrc && ${GRADLE_CMD} jar)"
        archiveArtifacts "buildSrc/build/libs/*.jar"
      }
    }

    stage('Deploy libs to SenX\' Nexus') {
      options {
        timeout(time: 2, unit: 'HOURS')
      }
      input {
        message "Should we deploy libs?"
      }
      steps {
        sh "(cd ./buildSrc && ${GRADLE_CMD} clean jar sourcesJar javadocJar publishMavenPublicationToNexusRepository -x test)"
      }
    }

    stage('Maven Publish') {
      when {
        expression { gitUtils.isTag() }
      }
      parallel {
        stage('Deploy to Maven Central') {
          options {
            timeout(time: 2, unit: 'HOURS')
          }
          input {
            message 'Should we deploy to Maven Central?'
          }
          steps {
            sh "(cd ./buildSrc && ${GRADLE_CMD} clean jar sourcesJar javadocJar publishMavenPublicationToMavenRepository -x test -x shadowJar)"
            sh "(cd ./buildSrc && ${GRADLE_CMD} closeRepository)"
            sh "(cd ./buildSrc && ${GRADLE_CMD} releaseRepository)"
            script {
              notify.slack('PUBLISHED')
            }
          }
        }
      }
    }
  }
  post {
    success {
      script {
        notify.slack('SUCCESSFUL')
      }
    }
    failure {
      script {
        notify.slack('FAILURE')
      }
    }
    aborted {
      script {
        notify.slack('ABORTED')
      }
    }
    unstable {
      script {
        notify.slack('UNSTABLE')
      }
    }
  }
}

String getVersion() {
  return sh(returnStdout: true, script: 'git describe --abbrev=0 --tags').trim()
}

boolean isItATagCommit() {
  String lastCommit = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()
  String tag = sh(returnStdout: true, script: "git show-ref --tags -d | grep ^${lastCommit} | sed -e 's,.* refs/tags/,,' -e 's/\\^{}//'").trim()
  return tag != ''
}

