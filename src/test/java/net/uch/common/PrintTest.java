package net.uch.common;

import net.uch.SpringConfigurationTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created for sistema_academico on 05/10/2024.
 *
 * @author Cesardl
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class PrintTest extends SpringConfigurationTest {

    @Autowired
    private Print print;

    @Test
    public void testCargarReporteSuccess() throws IOException {
        OutputStream out = mock(OutputStream.class);
        String jasper = "reports/report1.jrxml.jasper";

        URL resource = getClass().getClassLoader().getResource(jasper);
        assertNotNull(resource);
        when(externalContext.getResourceAsStream(anyString())).thenReturn(resource.openStream());

        print.cargarReporte(out, jasper, null);

        verify(out, times(1)).write(any());
    }

    @Test
    public void testCargarReporteFailed() throws IOException {
        OutputStream out = mock(OutputStream.class);
        Map<String, Object> params = Collections.emptyMap();
        String jasper = "img/user.png";

        URL resource = getClass().getClassLoader().getResource(jasper);
        assertNotNull(resource);
        when(externalContext.getResourceAsStream(anyString())).thenReturn(resource.openStream());

        print.cargarReporte(out, jasper, params);

        verify(out, never()).write(any());
    }
}
