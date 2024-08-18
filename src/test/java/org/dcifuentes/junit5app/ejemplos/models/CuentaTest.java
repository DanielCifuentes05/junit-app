package org.dcifuentes.junit5app.ejemplos.models;

import org.dcifuentes.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    Cuenta cuenta;

    @BeforeEach
    void initMetodoTest(){
        this.cuenta = new Cuenta("Daniel", new BigDecimal("1048.465"));
        System.out.println("inciando el metodo.");
    }

    @AfterEach
    void tearDown(){
        System.out.println("finalizando el metodo.");
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando el test.");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando el test.");
    }

    @Nested
    @DisplayName("probando atributos de la cuenta corriente")
    class CuentaTestNombreSaldo{
        @Test
        @DisplayName("Probando nombre")
        void testNombreCuenta() {

            //cuenta.setPersona("Daniel");
            String esperado = "Daniel";
            String real = cuenta.getPersona();

            assertNotNull(real,() -> "La cuenta no puede ser nula");
            assertEquals(esperado, real , () ->"El nombre de la cuenta no es la esperada:" +
                    "se esperaba " + esperado + " sin embargo fue " + real);
            assertTrue(real.equals(cuenta.getPersona()), () ->"El nombre de la cuenta esperada debe ser igual a la real");

        }

        @Test
        @DisplayName("Probando el saldo")
        void testSaldoCuenta() {

            assertEquals(1048.465, cuenta.getSaldo().doubleValue());
            assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
            assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

        }

        @Test
        @DisplayName("Testeando que las referencias sean iguales con el metodo equals.")
        void testReferenciaCuenta() {

            Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("555.47"));
            Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("555.47"));

            //assertNotEquals(cuenta2, cuenta);
            assertEquals(cuenta2, cuenta);

        }
    }

    @Nested
    class CuentaOperacionesTest{
        @Test
        void testDebitoCuenta() {
            cuenta.debito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(948, cuenta.getSaldo().intValue());
            assertEquals("948.465", cuenta.getSaldo().toPlainString());

        }

        @Test
        void testCreditoCuenta() {
            cuenta.credito(new BigDecimal(100));
            assertNotNull(cuenta.getSaldo());
            assertEquals(1148, cuenta.getSaldo().intValue());
            assertEquals("1148.465", cuenta.getSaldo().toPlainString());

        }

        @Test
        void testTransferirDineroCuentas() {
            Cuenta cuentaOrigen = new Cuenta("John Doe", new BigDecimal("1000.47"));
            Cuenta cuentaDestino = new Cuenta("Daniel Doe", new BigDecimal("1500.73"));
            Banco banco = new Banco();
            banco.setNombre("Banco del estado");
            banco.transferir(cuentaOrigen, cuentaDestino, new BigDecimal("500"));
            assertEquals("500.47", cuentaOrigen.getSaldo().toPlainString());
            assertEquals("2000.73", cuentaDestino.getSaldo().toPlainString());
        }
    }



    @Test
    void dineroInsuficienteExceptionCuenta() {
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1100.12"));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
    }



    @Test
    //@Disabled//Para ignorar este metodo/test
    @DisplayName("Probando relaciones entre cuentas y bancos con assertAll")
    void testRelacionBancoCuentas() {
        //fail(); //Fuerza el error
        Cuenta cuentaOrigen = new Cuenta("John Doe", new BigDecimal("1000.47"));
        Cuenta cuentaDestino = new Cuenta("Daniel Doe", new BigDecimal("1500.73"));

        Banco banco = new Banco();
        banco.addCuenta(cuentaOrigen);
        banco.addCuenta(cuentaDestino);
        banco.setNombre("Banco del estado");
        banco.transferir(cuentaOrigen, cuentaDestino, new BigDecimal("500"));
        assertAll(() -> assertEquals("500.47", cuentaOrigen.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta Origen no es la esperada"),
                () -> assertEquals("2000.73", cuentaDestino.getSaldo().toPlainString(),
                        () -> "El valor del saldo de la cuenta Destino no es la esperada"),
                () -> assertEquals(2, banco.getCuentas().size(),
                        () -> "El banco no tienes las cuentas esperadas"),
                () -> assertEquals("Banco del estado", cuentaOrigen.getBanco().getNombre()),
                () -> assertEquals("Daniel Doe", banco.getCuentas().stream()
                                .filter(c -> c.getPersona().equals("Daniel Doe"))
                                .findFirst()
                                .get().getPersona()),
                () -> assertTrue(banco.getCuentas().stream()
                                .anyMatch(c -> c.getPersona().equals("John Doe")))

        );

    }

    @Nested
    class SistemaOperativoTest{
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void testSoloWindows() {
        }

        @Test
        @EnabledOnOs({OS.LINUX , OS.MAC})
        void testSoloLinuxMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void testNoWindows() {
        }
    }

    @Nested
    class JavaVersionTest{
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void soloJdk8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_17)
        void soloJDK17() {
        }

        @Test
        @DisabledOnJre(JRE.JAVA_17)
        void testNoJDK17() {
        }
    }

    @Nested
    class SystemPropertiesTest{
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((k, v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfSystemProperty(named = "java.version", matches = ".*17.*")
        void testJavaVersion() {
        }

        @Test
        @DisabledIfSystemProperty(named = "os.arch" , matches = ".*32.*")
        void testSolo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "os.arch" , matches = ".*32.*")
        void testNo64() {
        }

        @Test
        @EnabledIfSystemProperty(named = "user.name", matches = "dcifu")
        void testUsername() {
        }

        @Test
        @EnabledIfSystemProperty(named = "ENV", matches = "dev")
        void testDev() {
        }
    }

    @Nested
    class VariablesAmbienteTest{
        @Test
        void imprimirVariablesAmbiente(){
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k,v) -> System.out.println(k + ":" + v));
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*jdk-17.*")
        void testJavaHome() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "NUMBER_OF_PROCESSORS", matches = "16")
        void testProcesadores() {
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "ENVIRONMENT" , matches = "dev")
        void testEnv() {
        }

        @Test
        @DisabledIfEnvironmentVariable(named = "ENVIRONMENT" , matches = "prod")
        void testEnvProdDisabled() {
        }
    }


    @Test
    @DisplayName("testSaldoCuentaDev")
    void testSaldoCuentaDev() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumeTrue(esDev);
        assertNotNull(cuenta.getSaldo());
        assertEquals(1048.465, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

    }

    @Test
    @DisplayName("2testSaldoCuentaDev")
    void testSaldoCuentaDev2() {
        boolean esDev = "dev".equals(System.getProperty("ENV"));
        assumingThat(esDev, () -> {
            assertNotNull(cuenta.getSaldo());
            assertEquals(1048.465, cuenta.getSaldo().doubleValue());

        });
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @DisplayName("Probando debito Cuenta Repetir")
    @RepeatedTest(value = 5, name = "{displayName} - Repetición numero {currentRepetition} de {totalRepetitions}")
    void testDebitoCuentaRepetir(RepetitionInfo info) {
        if(info.getCurrentRepetition() == 3){
            System.out.println("estamos en la repeticion " + info.getCurrentRepetition());
        }
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(948, cuenta.getSaldo().intValue());
        assertEquals("948.465", cuenta.getSaldo().toPlainString());
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {0} - {argumentsWithNames}")
    @ValueSource(doubles = {100,200,300,500,700,1048.465})
    void testDebitoCuenta(double monto) {
        cuenta.debito(new BigDecimal(monto));
        assertNotNull(cuenta.getSaldo());
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO)>0);

    }
}