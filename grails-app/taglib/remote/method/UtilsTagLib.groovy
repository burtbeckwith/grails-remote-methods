package remote.method

import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.context.ApplicationContext

class UtilsTagLib {
    def grailsApplication

    Closure defineRemote = { attrs, body ->
        javascript(null, {
            def controller = getController(attrs.controller)
            def methods = getMethods(controller, attrs.methods)

            methods.each {
                out << "var ${controller.controllerName} = {"
                out << "${it}:function (params, success, error) {"
                out << remoteFunction(
                        controller: controller.controllerName,
                        action: it,
                        params: "params",
                        onSuccess: "success(data, textStatus)",
                        onFailure: """
                    if (error) {
                        error(XMLHttpRequest,textStatus,errorThrown)
                    } else {
                        window.errorHandler(XMLHttpRequest,textStatus,errorThrown)
                    }
                    """
                )
                out << "}"
            }
            out << "}"

            null
        })
    }

    private def getController(def name) {
        ApplicationContext applicationContext = (ApplicationContext) ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
        return applicationContext.getBean(
                grailsApplication.getArtefactByLogicalPropertyName("Controller", name ?: controllerName).clazz.name
        )
    }

    private def getMethods(def controller, def methods) {
        if (controller.properties['methods'] == null && methods == null) {
            throw new IllegalArgumentException("In tag 'defineRemote' you should specify methods attribute in tag or define list methods in controller")
        }

        methods ?: controller.methods
    }
}
