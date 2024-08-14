package org.dcifuentes.junit5app.ejemplos.models;

import org.dcifuentes.junit5app.ejemplos.exceptions.DineroInsuficienteException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CuentaTest {

    @Test
    void testNombreCuenta() {
        Cuenta cuenta = new Cuenta("Daniel", new BigDecimal("1048.465"));

        //cuenta.setPersona("Daniel");
        String esperado = "Daniel";
        String real = cuenta.getPersona();

        assertNotNull(real,() -> "La cuenta no puede ser nula");
        assertEquals(esperado, real , () ->"El nombre de la cuenta no es la esperada:" +
                "se esperaba " + esperado + " sin embargo fue " + real);
        assertTrue(real.equals(cuenta.getPersona()), () ->"El nombre de la cuenta esperada debe ser igual a la real");

    }

    @Test
    void testSaldoCuenta() {
        Cuenta cuenta = new Cuenta("Daniel", new BigDecimal("1005.486"));
        assertEquals(1005.486, cuenta.getSaldo().doubleValue());
        assertFalse(cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
        assertTrue(cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);

    }

    @Test
    void testReferenciaCuenta() {

        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("555.47"));
        Cuenta cuenta2 = new Cuenta("John Doe", new BigDecimal("555.47"));

        //assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, cuenta);

    }

    @Test
    void testDebitoCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.47"));
        cuenta.debito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(900, cuenta.getSaldo().intValue());
        assertEquals("900.47", cuenta.getSaldo().toPlainString());

    }

    @Test
    void testCreditoCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.47"));
        cuenta.credito(new BigDecimal(100));
        assertNotNull(cuenta.getSaldo());
        assertEquals(1100, cuenta.getSaldo().intValue());
        assertEquals("1100.47", cuenta.getSaldo().toPlainString());

    }

    @Test
    void dineroInsuficienteExceptionCuenta() {
        Cuenta cuenta = new Cuenta("John Doe", new BigDecimal("1000.47"));
        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            cuenta.debito(new BigDecimal("1100.12"));
        });

        String actual = exception.getMessage();
        String esperado = "Dinero Insuficiente";
        assertEquals(esperado, actual);
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

    @Test
    void testRelacionBancoCuentas() {
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
}