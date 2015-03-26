package retrieval.exception;
/*
 * Copyright 2015 ROLLUS Loïc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * A CBIR exception
 * @author Rollus Loic
 */
public class CBIRException extends Exception {

    /**
     * Code for the CBIRException
     */
    String code;

    /**
     * Constructor of CBIRException
     * @param code Error code for exception
     * @param message Message for exception
     */
    public CBIRException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructor of CBIRException
     * @param msg Message for exception
     */
    public CBIRException(String msg) {
        super(msg);
    }

    /**
     * Get code of CBIRException
     * @return Code
     */
    public String getCode()
    {
        return code;
    }
    
    public boolean isNotAnException() {
        return "1000".equals(code);
    }

}
