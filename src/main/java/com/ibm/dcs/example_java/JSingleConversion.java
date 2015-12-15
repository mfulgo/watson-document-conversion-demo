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
package com.ibm.dcs.example_java;

import com.ibm.watson.developer_cloud.document_conversion.v1.DocumentConversion;

import java.io.File;

import static com.ibm.dcs.Environment.DEFAULT;

public class JSingleConversion {

    public static void main(String[] args) {
        File book = new File("alices_adventures_in_wonderland.pdf");

        DocumentConversion service = new DocumentConversion();
        service.setUsernameAndPassword(DEFAULT.getUsername(), DEFAULT.getPassword());

        String text = service.convertDocumentToText(book);

        System.out.println(text.substring(0, 1024*10));
    }
}
