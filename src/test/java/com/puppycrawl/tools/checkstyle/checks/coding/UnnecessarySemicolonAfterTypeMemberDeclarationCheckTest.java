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

package com.puppycrawl.tools.checkstyle.checks.coding;

import static com.puppycrawl.tools.checkstyle.checks.coding.UnnecessarySemicolonAfterTypeMemberDeclarationCheck.MSG_SEMI;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class UnnecessarySemicolonAfterTypeMemberDeclarationCheckTest
    extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/puppycrawl/tools/checkstyle/checks/coding/"
            + "unnecessarysemicolonaftertypememberdeclaration";
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnnecessarySemicolonAfterTypeMemberDeclarationCheck.class);

        final String[] expected = {
            "13:5: " + getCheckMessage(MSG_SEMI),
            "15:21: " + getCheckMessage(MSG_SEMI),
            "17:14: " + getCheckMessage(MSG_SEMI),
            "19:60: " + getCheckMessage(MSG_SEMI),
            "21:14: " + getCheckMessage(MSG_SEMI),
            "23:20: " + getCheckMessage(MSG_SEMI),
            "25:19: " + getCheckMessage(MSG_SEMI),
            "27:15: " + getCheckMessage(MSG_SEMI),
            "29:23: " + getCheckMessage(MSG_SEMI),
            "31:15: " + getCheckMessage(MSG_SEMI),
            "34:13: " + getCheckMessage(MSG_SEMI),
            "46:5: " + getCheckMessage(MSG_SEMI),
            "49:5: " + getCheckMessage(MSG_SEMI),
            "52:20: " + getCheckMessage(MSG_SEMI),
        };

        verifyWithInlineConfigParser(checkConfig,
            getPath("InputUnnecessarySemicolonAfterTypeMemberDeclaration.java"),
            expected);
    }

    @Test
    public void testUnnecessarySemicolonAfterTypeMemberDeclarationRecords() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(UnnecessarySemicolonAfterTypeMemberDeclarationCheck.class);

        final String[] expected = {
            "14:5: " + getCheckMessage(MSG_SEMI),
            "18:5: " + getCheckMessage(MSG_SEMI),
            "22:5: " + getCheckMessage(MSG_SEMI),
            "27:5: " + getCheckMessage(MSG_SEMI),
            "33:5: " + getCheckMessage(MSG_SEMI),
            "38:5: " + getCheckMessage(MSG_SEMI),
            "40:5: " + getCheckMessage(MSG_SEMI),
        };

        verifyWithInlineConfigParser(checkConfig,
            getNonCompilablePath("InputUnnecessarySemicolonAfterTypeMemberDeclarationRecords.java"),
            expected);
    }

    @Test
    public void testTokens() {
        final UnnecessarySemicolonAfterTypeMemberDeclarationCheck check =
            new UnnecessarySemicolonAfterTypeMemberDeclarationCheck();
        final int[] expected = {
            TokenTypes.CLASS_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ANNOTATION_DEF,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.ANNOTATION_FIELD_DEF,
            TokenTypes.STATIC_INIT,
            TokenTypes.INSTANCE_INIT,
            TokenTypes.CTOR_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.ENUM_CONSTANT_DEF,
            TokenTypes.COMPACT_CTOR_DEF,
            TokenTypes.RECORD_DEF,
        };
        assertArrayEquals(expected, check.getAcceptableTokens(),
                "Acceptable required tokens are invalid");
        assertArrayEquals(expected, check.getDefaultTokens(),
                "Default required tokens are invalid");
        assertArrayEquals(CommonUtil.EMPTY_INT_ARRAY, check.getRequiredTokens(),
                "Required required tokens are invalid");
    }
}
