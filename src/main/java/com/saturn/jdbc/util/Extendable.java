/*
 * Extendable.java
 *
 * Created on 2007��8��23��, ����9:28
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.saturn.jdbc.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
public @interface Extendable {
}