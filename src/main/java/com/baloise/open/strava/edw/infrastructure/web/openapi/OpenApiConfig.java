package com.baloise.open.strava.edw.infrastructure.web.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(title = "${springdoc.api-info.title}",
                description = "${springdoc.api-info.description}",
                version = "${springdoc.api-info.version}",
                contact = @Contact(name = "${springdoc.api-info.contact.name}",
                        email = "${springdoc.api-info.contact.email}",
                        url = "${springdoc.api-info.contact.url}"),
                extensions = @Extension(
                        properties = {
                                @ExtensionProperty(name = "x-audience", value = "${springdoc.api-info.header.audience}"),
                                @ExtensionProperty(name = "x-api-id", value = "${springdoc.api-info.header.api-id}"),
                        }
                )),
        servers = {@Server(url = "${server.servlet.context-path}", description = "Local, used for development.")}
)
//@SecurityScheme(
//        name = "security_auth",
//        description = "Used for complete internal access over RHSSO",
//        type = SecuritySchemeType.OAUTH2,
//        flows = @OAuthFlows(
//                authorizationCode = @OAuthFlow(
//                        authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}",
//                        tokenUrl = "${springdoc.oAuthFlow.tokenUrl}",
//                        refreshUrl = "${springdoc.oAuthFlow.refreshUrl}"
//                )
//        )
//)
//@SecurityScheme(
//        name = "api_key_auth",
//        description = "Used for access over the Enterprise Gateway (Layer7)",
//        type = SecuritySchemeType.APIKEY,
//        in = SecuritySchemeIn.HEADER,
//        paramName = "x-api-key"
//)
public class OpenApiConfig {
}
