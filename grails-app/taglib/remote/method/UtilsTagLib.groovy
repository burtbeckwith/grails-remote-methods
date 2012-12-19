package remote.method

class UtilsTagLib {

    def grailsApplication

    def defineRemote = { attrs ->
        javascript(null, {
            def controller = getController(attrs.controller)
            def methods = getMethods(controller, attrs.methods)

            out << "var ${controller.controllerName} = {"
            methods.each {
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
                out << "},"
            }
            out << "_:0}"

            null
        })
    }

    private getController(name) {
        return grailsApplication.mainContext.getBean(
                grailsApplication.getArtefactByLogicalPropertyName("Controller", name ?: controllerName).clazz.name
        )
    }

    private getMethods(controller, methods) {
        if (controller.hasProperty('remoteMethods') == null && methods == null) {
            throw new IllegalArgumentException("In tag 'defineRemote' you should specify methods attribute in tag or define list methods in controller")
        }

        methods ?: controller.remoteMethods
    }
}
