package schumi178.javaprograms.cheatbuster.test;

import org.junit.jupiter.api.Test;
import schumi178.javaprograms.cheatbuster.util.TypedefParser;

import java.util.List;

public class TypedefParsingTest {
    @Test
    public void parseTypedefSimple() {
        String statement1 = "typedef int aType;";
        List<String> result1 = TypedefParser.getDefinedTypes(statement1);
        assert result1.size() == 1;
        assert result1.get(0).equals("aType");

        String statement2 = "typedef anotherType yetAnotherType;";
        List<String> result2 = TypedefParser.getDefinedTypes(statement2);
        assert result2.size() == 1;
        assert result2.get(0).equals("yetAnotherType");
    }

    @Test
    public void parseTypedefMultipleAlias() {
        String statement1 = "typedef unsigned char UCHAR, *PUCHAR;";
        List<String> result1 = TypedefParser.getDefinedTypes(statement1);
        assert result1.size() == 2;
        assert result1.contains("UCHAR");
        assert result1.contains("PUCHAR");

        String statement2 = "typedef signed long long int LINT, anotherLINT, *PLINT, *anotherPLINT;";
        List<String> result2 = TypedefParser.getDefinedTypes(statement2);
        assert result2.size() == 4;
        assert result2.contains("LINT");
        assert result2.contains("anotherLINT");
        assert result2.contains("PLINT");
        assert result2.contains("anotherPLINT");
    }

    @Test
    public void parseTypedefStruct() {
        String statement1 = "typedef struct {\nint aNumber;\nstruct dirent *ptr;\nbool value;\n} SomeStruct, *SomeStructPtr;";
        List<String> result1 = TypedefParser.getDefinedTypes(statement1);
        assert result1.size() == 2;
        assert result1.contains("SomeStruct");
        assert result1.contains("SomeStructPtr");

        String statement2 = "typedef struct foo {\nint one;\nlong two;\nchar three;\n} *bar;";
        List<String> result2 = TypedefParser.getDefinedTypes(statement2);
        assert result2.size() == 1;
        assert result2.get(0).equals("bar");
    }

    @Test
    public void parseTypedefExtraSpecifiers() {
        String statement1 = "typedef _Complex float __cfloat128 __attribute__ ((__mode__ (__TC__)));";
        List<String> result1 = TypedefParser.getDefinedTypes(statement1);
        assert result1.size() == 1;
        assert result1.get(0).equals("__cfloat128");
    }

    @Test
    public void parseTypedefFunctionAlias() {
        String statement1 = "typedef __ssize_t cookie_write_function_t (void *__cookie, const char *__buf,\nsize_t __nbytes);";
        List<String> result1 = TypedefParser.getDefinedTypes(statement1);
        assert result1.size() == 1;
        assert result1.get(0).equals("cookie_write_function_t");

        String statement2 = "typedef __ssize_t (cookie_write_function_t)(void *__cookie, const char *__buf,\nsize_t __nbytes);";
        List<String> result2 = TypedefParser.getDefinedTypes(statement2);
        assert result2.size() == 1;
        assert result2.get(0).equals("cookie_write_function_t");

        String statement3 = "typedef __ssize_t (*cookie_write_function_t)(void *__cookie, const char *__buf,\nsize_t __nbytes);";
        List<String> result3 = TypedefParser.getDefinedTypes(statement3);
        assert result3.size() == 1;
        assert result3.get(0).equals("cookie_write_function_t");
    }
}
