////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2021 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.puppycrawl.tools.checkstyle;


import org.junit.Test;

import java.io.IOException;

public class MainTestCus {

    @Test
    public void testCustomFile() throws IOException {
//        java
//        -jar
//        -Xbootclasspath/a:"C:\Users\Ming\Desktop\test\ICMS-PC\src\main\webapp\WEB-INF\lib\checkstyle-9.1-SNAPSHOT-all.jar"
//        "C:\Users\Ming\Desktop\test\ICMS-PC\src\main\webapp\WEB-INF\lib\checkstyle-9.1-SNAPSHOT-all.jar"
//        -c C:\Users\Ming\Desktop\test\ICMS-PC\sun_checks.xml
//        src/main/java/com/shihe/image/center/aspect/Checkstyle.java


        Main.main(
                "-c",
                "C:\\Users\\Ming\\Desktop\\test\\ICMS-PC\\sun_checks.xml",
                "src/test/resources/com/puppycrawl/tools/checkstyle/main/InputMain.java"
        );
    }

    private static String getPath(String filename) {
        return "" + filename;
    }


}
