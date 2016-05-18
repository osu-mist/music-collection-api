/* CodeNarc RuleSet
   http://codenarc.sourceforge.net/codenarc-rule-index.html */

ruleset {
    description 'MIST Groovy RuleSet'

    /* Verify that class name starts with an uppercase letter followed by zero or
       more word characters or dollar signs. */
    ClassName

    /* method name starts with a lowercase letter */
    MethodName

    PackageName {
        description = 'Verify that package name begins with \'edu.oregonstate.mist.\' and consists ' +
                      'only of lowercase letters and numbers separated by periods.'
        regex = '''(?x)
                   ^(
                     edu\\.oregonstate\\.mist # begin with edu.oregonstate.mist
                     (\\.[a-z0-9]+)*          # periods separate lowercase alphanumeric package names
                   )$'''
        packageNameRequired = true
        priority = 3
    }

    /* package name consists only of lowercase letters and numbers, separated
       by periods */
    PackageName

    /* semicolons as line terminators are not required in Groovy */
    UnnecessarySemicolon

    /* Checks that if statements use braces, even for a single statement. */
    IfStatementBraces

    /* Checks that else blocks use braces, even for a single statement. */
    ElseBlockBraces

    /* Checks that for statements use braces, even for a single statement. */
    ForStatementBraces

    /* Checks that while statements use braces, even for a single statement. */
    WhileStatementBraces

    /* Makes sure there are no consecutive lines that are either blank or whitespace only. */
    ConsecutiveBlankLines

    /* Check that there is at least one space before each opening brace */
    SpaceBeforeOpeningBrace

    /* Check that there is at least one space after each closing brace */
    SpaceAfterClosingBrace
}
