/**
 * Copyright 2015 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.ibm.dcs;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Optional;
import java.util.stream.Stream;

public class Environment {
    public static final Environment DEFAULT = new Environment(ConfigFactory.load());

    private final Config config;
    private final Optional<String> credentialsName;
    private final Optional<Config> dcsVcapCredentials;

    public Environment(Config config) {
        this.config = config;
        Optional<Config> vcapServices = config.hasPath("vcap_services") ?
                Optional.of(config.getConfig("vcap_services")) : Optional.empty();

        credentialsName = getOptionalString(config, "dcs.vcap_credentials_name");

        dcsVcapCredentials = vcapServices.flatMap(services -> {
            if (!services.hasPath("document-conversion")) return Optional.empty();
            else {
                Stream<? extends Config> instances = services.getConfigList("document-conversion").stream();
                // Get the named credentials or the first if no name is specified
                Optional<? extends Config> instance = credentialsName.isPresent() ?
                        credentialsName.flatMap(c -> findInstanceByName(instances, c)) : instances.findFirst();
                return instance.map(i -> i.getConfig("credentials"));
            }
        });
    }

    private Optional<? extends Config> findInstanceByName(Stream<? extends Config> instances, String name) {
        return instances
                .filter(instance -> name.equals(instance.getString("name")))
                .findFirst();
    }

    private Optional<String> getOptionalString(Config config, String path) {
        return config.hasPath(path) ? Optional.of(config.getString(path)) : Optional.empty();
    }

    public String getUsername() {
        return getOptionalString(config, "dcs.username").orElseGet(() ->
                dcsVcapCredentials.flatMap(c -> getOptionalString(c, "username")).orElseThrow(() ->
                        new IllegalStateException("No username was specified!")));
    }

    public String getPassword() {
        return getOptionalString(config, "dcs.password").orElseGet(() ->
                dcsVcapCredentials.flatMap(c -> getOptionalString(c, "password")).orElseThrow(() ->
                        new IllegalStateException("No password was specified!")));
    }
}
