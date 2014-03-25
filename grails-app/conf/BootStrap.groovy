import grails.util.Environment
import groovy.util.logging.Log4j

@Log4j
class BootStrap {

    final static String TEST_PHASE_CONFIGURER_CLASS_NAME =
            'org.codehaus.groovy.grails.test.runner.phase.IntegrationTestPhaseConfigurer'

    def init = { servletContext ->
        if (Environment.currentEnvironment == Environment.TEST) {
            if (Class.forName(TEST_PHASE_CONFIGURER_CLASS_NAME).currentApplicationContext) {
                /* don't load the test data bundle for integration tests */
                return
            }
            def testData = createTestData()
            log.info 'About to save test data'
            testData.saveAll()
        }
    }

    def createTestData() {
        Class clazz = Class.forName('org.transmartproject.db.TestData')
        clazz.getMethod('createDefault').invoke(null) //static method
    }
}
