#!/usr/bin/env bash

echo "
<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\"
    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
    xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd\">
  <interactiveMode>false</interactiveMode>
  <proxies></proxies>
  <servers>
    <server>
      <id>verygood-release-repo</id>
      <!-- Bash will substitute in the environment variable values when echoing. -->
      <username>$AWS_ACCESS_KEY_ID</username>
      <password>$AWS_SECRET_ACCESS_KEY</password>
    </server>
    <server>
      <id>verygood-snapshot-repo</id>
      <username>$AWS_ACCESS_KEY_ID</username>
      <password>$AWS_SECRET_ACCESS_KEY</password>
    </server>
    <server>
      <id>artifactory-central</id>
      <username>$ARTIFACTORY_USERNAME</username>
      <password>$ARTIFACTORY_PASSWORD</password>
    </server>
    <server>
      <id>artifactory-jcenter</id>
      <username>$ARTIFACTORY_USERNAME</username>
      <password>$ARTIFACTORY_PASSWORD</password>
    </server>
    <server>
      <id>artifactory-release</id>
      <username>$ARTIFACTORY_USERNAME</username>
      <password>$ARTIFACTORY_PASSWORD</password>
    </server>
    <server>
      <id>artifactory-snapshot</id>
      <username>$ARTIFACTORY_USERNAME</username>
      <password>$ARTIFACTORY_PASSWORD</password>
    </server>
  </servers>
  <mirrors>
    <mirror>
      <mirrorOf>central</mirrorOf>
      <id>artifactory-central</id>
      <url>https://vgs.jfrog.io/artifactory/central/</url>
    </mirror>
    <mirror>
      <mirrorOf>jcenter</mirrorOf>
      <id>artifactory-jcenter</id>
      <url>https://vgs.jfrog.io/artifactory/jcenter/</url>
    </mirror>
  </mirrors>
  <activeProfiles>
    <activeProfile>very-good-security</activeProfile>
  </activeProfiles>
  <profiles>
    <profile>
      <id>very-good-security</id>
      <activation>
        <activeByDefault>false</activeByDefault>
      </activation>
      <repositories>
        <repository>
          <id>verygood-release-repo</id>
          <name>Very Good Release Repository</name>
          <url>s3://vault-dev-01-audits-01-artifact-19k6160zpr44j/software/release/</url>
          <releases>
            <enabled>true</enabled>
          </releases>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
        </repository>
        <repository>
          <id>verygood-snapshot-repo</id>
          <name>Very Good Snapshot Repository</name>
          <url>s3://vault-dev-01-audits-01-artifact-19k6160zpr44j/software/snapshot/</url>
          <releases>
            <enabled>false</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
        <repository>
          <id>artifactory-release</id>
          <name>VGS Artifactory Releases</name>
          <url>https://vgs.jfrog.io/artifactory/libs-release/</url>
          <releases>
            <enabled>true</enabled>
          </releases>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
        </repository>
        <repository>
          <id>artifactory-snapshot</id>
          <name>VGS Artifactory Snapshots</name>
          <url>https://vgs.jfrog.io/artifactory/libs-snapshot/</url>
          <releases>
            <enabled>false</enabled>
          </releases>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </repository>
      </repositories>
      <pluginRepositories>
        <pluginRepository>
          <id>bintray-vg-vgs-oss-plugins</id>
          <url>https://dl.bintray.com/vg/vgs-oss</url>
          <snapshots>
            <enabled>true</enabled>
          </snapshots>
        </pluginRepository>
      </pluginRepositories>
    </profile>
  </profiles>
</settings>
" > ~/.m2/settings.xml
