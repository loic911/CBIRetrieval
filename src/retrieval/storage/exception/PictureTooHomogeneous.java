/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package retrieval.storage.exception;

import retrieval.exception.CBIRException;

/**
 *
 * @author lrollus
 */
public class PictureTooHomogeneous extends CBIRException {
    /**
     * Error Code
     */
    public static final String CODE = "1121";

    /**
     * Creates a new instance of <code>AlreadyInPuctureIndexException</code> without detail message.
     */
    public PictureTooHomogeneous() {
        super(CODE,"");
    }


    /**
     * Constructs an instance of <code>AlreadyInPuctureIndexException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public PictureTooHomogeneous(String msg) {
        super(CODE,msg);
    }
}
