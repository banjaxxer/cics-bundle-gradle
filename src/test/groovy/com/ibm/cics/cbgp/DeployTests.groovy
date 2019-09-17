package com.ibm.cics.cbgp

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome

/*-
 * #%L
 * CICS Bundle Gradle Plugin
 * %%
 * Copyright (C) 2019 IBM Corp.
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 */

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.FAILED
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DeployTests extends Specification {

	@Rule
	public TemporaryFolder testProjectDir = new TemporaryFolder()
	File settingsFile
	File buildFile

	def setup() {
		ExpandoMetaClass.disableGlobally()
		settingsFile = testProjectDir.newFile('settings.gradle')
		settingsFile << "rootProject.name = 'cics-bundle-gradle'"
		buildFile = testProjectDir.newFile('build.gradle')
	}

	def "Test missing deploy extension block"() {
		given:
		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            version '1.0.0-SNAPSHOT'
            
            repositories {
                jcenter()
            }
            
            dependencies {
                ${BundlePlugin.BUNDLE_DEPENDENCY_CONFIGURATION_NAME}('javax.servlet:javax.servlet-api:3.1.0@jar')
            }
        """

		when:
		def result = runGradle('Test missing deploy extension block', [BundlePlugin.DEPLOY_TASK_NAME], true)

		then:
		checkResults(result, [DeployBundleTask.MISSING_CONFIG, DeployBundleTask.PLEASE_SPECIFY], [], FAILED)
	}

	def "Test empty deploy extension block"() {
		given:
		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            version '1.0.0-SNAPSHOT'
            
            ${BundlePlugin.DEPLOY_EXTENSION_NAME} {
            }

        """

		when:
		def result = runGradle('Test empty deploy extension block', [BundlePlugin.DEPLOY_TASK_NAME], true)

		then:
		checkResults(result, [DeployBundleTask.MISSING_CONFIG, DeployBundleTask.PLEASE_SPECIFY], [], FAILED)
	}

	def "Test missing cicsplex"() {
		given:
		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            ${BundlePlugin.DEPLOY_EXTENSION_NAME} {
                region = 'MYEGION'
                bunddef = 'MYDEF'
                csdgroup = 'MYGROUP'
                url = 'someurl'
                username = 'bob'
                password = 'passw0rd'
            }
        """

		when:
		def result = runGradle('Test missing cicsplex', [BundlePlugin.DEPLOY_TASK_NAME], true)

		then:
		checkResults(result, [DeployBundleTask.MISSING_CICSPLEX, DeployBundleTask.PLEASE_SPECIFY], [], FAILED)
	}

	def "Test missing region"() {
		given:
		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            version '1.0.0-SNAPSHOT'
            
            repositories {
                jcenter()
            }
            
            ${BundlePlugin.DEPLOY_EXTENSION_NAME} {
                cicsplex            = 'MYPLEX'
                bunddef             = 'MYDEF'
                csdgroup            = 'MYGROUP'
                url                 = 'someurl'
                username            = 'bob'
                password            = 'passw0rd'
            }
            
            dependencies {
                ${BundlePlugin.BUNDLE_DEPENDENCY_CONFIGURATION_NAME}('javax.servlet:javax.servlet-api:3.1.0@jar')
            }
        """

		when:
		def result = runGradle('Test missing region', [BundlePlugin.DEPLOY_TASK_NAME], true)

		then:
		checkResults(result, [DeployBundleTask.MISSING_REGION, DeployBundleTask.PLEASE_SPECIFY], [], FAILED)
	}

	def "Test missing bunddef"() {
		given:
		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            version '1.0.0-SNAPSHOT'
            
            repositories {
                jcenter()
            }
            
            ${BundlePlugin.DEPLOY_EXTENSION_NAME} {
                cicsplex            = 'MYPLEX'
                region              = 'MYEGION'
                csdgroup            = 'MYGROUP'
                url                 = 'someurl'
                username            = 'bob'
                password            = 'passw0rd'
            }
            
            dependencies {
                ${BundlePlugin.BUNDLE_DEPENDENCY_CONFIGURATION_NAME}('javax.servlet:javax.servlet-api:3.1.0@jar')
            }
        """

		when:
		def result = runGradle('Test missing bunddef', [BundlePlugin.DEPLOY_TASK_NAME], true)

		then:
		checkResults(result, [DeployBundleTask.MISSING_BUNDDEF, DeployBundleTask.PLEASE_SPECIFY], [], FAILED)
	}

	def "Test missing csdgroup"() {
		given:
		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            version '1.0.0-SNAPSHOT'
            
            repositories {
                jcenter()
            }
            
            ${BundlePlugin.DEPLOY_EXTENSION_NAME} {
                cicsplex            = 'MYPLEX'
                region              = 'MYEGION'
                bunddef             = 'MYDEF'
                url                 = 'someurl'
                username            = 'bob'
                password            = 'passw0rd'
            }
            
            dependencies {
                ${BundlePlugin.BUNDLE_DEPENDENCY_CONFIGURATION_NAME}('javax.servlet:javax.servlet-api:3.1.0@jar')
            }
        """

		when:
		def result = runGradle('Test missing csdgroup', [BundlePlugin.DEPLOY_TASK_NAME], true)

		then:
		checkResults(result, [DeployBundleTask.MISSING_CSDGROUP, DeployBundleTask.PLEASE_SPECIFY], [], FAILED)
	}

	def "Test missing url"() {
		given:
		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            version '1.0.0-SNAPSHOT'
            
            repositories {
                jcenter()
            }
            
            ${BundlePlugin.DEPLOY_EXTENSION_NAME} {
                cicsplex            = 'MYPLEX'
                region              = 'MYEGION'
                bunddef             = 'MYDEF'
                csdgroup            = 'MYGROUP'
                username = 'bob'
                password = 'passw0rd'
            }
            
            dependencies {
                ${BundlePlugin.BUNDLE_DEPENDENCY_CONFIGURATION_NAME}('javax.servlet:javax.servlet-api:3.1.0@jar')
            }
        """

		when:
		def result = runGradle('Test missing url', [BundlePlugin.DEPLOY_TASK_NAME], true)

		then:
		checkResults(result, [DeployBundleTask.MISSING_URL, DeployBundleTask.PLEASE_SPECIFY], [], FAILED)
	}

	def "Test missing username"() {
		given:
		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            version '1.0.0-SNAPSHOT'
            
            repositories {
                jcenter()
            }
            
            ${BundlePlugin.DEPLOY_EXTENSION_NAME} {
                cicsplex            = 'MYPLEX'
                region              = 'MYEGION'
                bunddef             = 'MYDEF'
                csdgroup            = 'MYGROUP'
                url                 = 'someurl'
                password            = 'passw0rd'
            }
            
            dependencies {
                ${BundlePlugin.BUNDLE_DEPENDENCY_CONFIGURATION_NAME}('javax.servlet:javax.servlet-api:3.1.0@jar')
            }
        """

		when:
		def result = runGradle('Test missing username', [BundlePlugin.DEPLOY_TASK_NAME], true)

		then:
		checkResults(result, [DeployBundleTask.MISSING_USERNAME, DeployBundleTask.PLEASE_SPECIFY], [], FAILED)
	}

	def "Test missing password"() {
		given:
		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            version '1.0.0-SNAPSHOT'
            
            repositories {
                jcenter()
            }
            
            ${BundlePlugin.DEPLOY_EXTENSION_NAME} {
                cicsplex            = 'MYPLEX'
                region              = 'MYEGION'
                bunddef             = 'MYDEF'
                csdgroup            = 'MYGROUP'
                url                 = 'someurl'
                username            = 'bob'
            }
            
            dependencies {
                ${BundlePlugin.BUNDLE_DEPENDENCY_CONFIGURATION_NAME}('javax.servlet:javax.servlet-api:3.1.0@jar')
            }
        """

		when:
		def result = runGradle('Test missing password', [BundlePlugin.DEPLOY_TASK_NAME], true)

		then:
		checkResults(result, [DeployBundleTask.MISSING_PASSWORD, DeployBundleTask.PLEASE_SPECIFY], [], FAILED)
	}

	def "Test multiple items missing"() {
		given:
		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            version '1.0.0-SNAPSHOT'
            
            repositories {
                jcenter()
            }
            
            ${BundlePlugin.DEPLOY_EXTENSION_NAME} {
                cicsplex            = 'MYPLEX'
                csdgroup            = 'MYGROUP'
                url                 = 'someurl'
                username            = 'bob'
                password            = 'passw0rd'
            }
            
            dependencies {
                ${BundlePlugin.BUNDLE_DEPENDENCY_CONFIGURATION_NAME}('javax.servlet:javax.servlet-api:3.1.0@jar')
            }
        """

		when:
		def result = runGradle('Test multiple items missing', [BundlePlugin.DEPLOY_TASK_NAME], true)

		then:
		checkResults(result, [
				DeployBundleTask.MISSING_REGION,
				DeployBundleTask.MISSING_BUNDDEF,
				DeployBundleTask.PLEASE_SPECIFY
		], [], FAILED)
	}

	def "Test substitute username and password"() {
		given:
		File propertiesFile = testProjectDir.newFile('gradle.properties')
		propertiesFile << """\
            my_username=alice
            password=secret
        """

		buildFile << """\
            plugins {
                id 'cics-bundle-gradle-plugin'
            }
            
            version '1.0.0-SNAPSHOT'
            
            repositories {
                jcenter()
            }
            
            ${BundlePlugin.DEPLOY_EXTENSION_NAME} {
                region   = 'MYEGION'
                cicsplex = 'MYPLEX'
                bunddef  = 'MYDEF'
                csdgroup = 'MYGROUP'
                url      = 'someurl'
                username = my_username      // Define my_username in gradle.properties
                password = project.properties['password']  // same name for variable and gradle.properties entry
            }
        """

		when:
		def result = runGradle('Test substitute username and password', [BundlePlugin.DEPLOY_TASK_NAME])

		then:
		checkResults(result, [], [], SUCCESS)
	}

	// Run the gradle build with defaults and print the test output
	def runGradle(String testName, List args = [BundlePlugin.DEPLOY_TASK_NAME], boolean failExpected = false) {
		def result
		if (!failExpected) {
			result = GradleRunner.create().withProjectDir(testProjectDir.root).withArguments(args).withPluginClasspath().build()
		} else {
			result = GradleRunner.create().withProjectDir(testProjectDir.root).withArguments(args).withPluginClasspath().buildAndFail()
		}
		def title = "\n----- '$testName' output: -----"
		println(title)
		println(result.output)
		println('-' * title.length())
		println()
		return result
	}

	def checkResults(BuildResult result, List resultStrings, List outputFiles, TaskOutcome outcome) {
		resultStrings.each {
			if (!result.output.contains(it)) {
				println("Not found in build output: '$it'")
				assert (false)
			}
		}
		outputFiles.each {
			if (!getFileInBuildOutputFolder(it).exists()) {
				println("File not found in output folder: '$it'")
				assert (false)
			}
		}
		result.task(":$BundlePlugin.DEPLOY_TASK_NAME").outcome == outcome
	}

	private File getFileInBuildOutputFolder(String fileName) {
		return new File(buildFile.parent + '/build/cics-bundle-gradle-1.0.0-SNAPSHOT/' + fileName)
	}

	// Some useful functions as I can't get debug to work for Gradle Runner tests yet
	private void printTemporaryFileTree() {
		def tempFolder = new File(buildFile.parent)

		println("  Temp file tree: $tempFolder  ----")
		tempFolder.traverse {
			println('   ' + it.path)
		}
		println('  -----')
	}

}
