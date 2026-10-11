package net.uch.administrativa.service.impl;

import net.uch.administrativa.dao.HSAlumnoTarifaDAO;
import net.uch.administrativa.dao.HSProrrogaDAO;
import net.uch.administrativa.service.ProrrogaService;
import net.uch.administrativa.service.ProrrogaValidationException;
import net.uch.mapping.AdAlumnoTarifa;
import net.uch.mapping.AdProrroga;
import net.uch.mapping.TbUsuario;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Wired explicitly (constructor-injected DAO proxies) in {@code applicationContext-services.xml},
 * like the other services - no component-scan on this stack. {@link #registerExtension} is
 * {@code @Transactional} so the {@code ad_prorroga} insert and the {@code alutar_fecha_prorroga}
 * update commit together - the "transactionally linked" guarantee that was missing before #45/#33.
 *
 * @author cesardl
 */
public class ProrrogaServiceImpl implements ProrrogaService {

    private final HSProrrogaDAO prorrogaDao;
    private final HSAlumnoTarifaDAO alumnoTarifaDao;

    public ProrrogaServiceImpl(HSProrrogaDAO prorrogaDao, HSAlumnoTarifaDAO alumnoTarifaDao) {
        this.prorrogaDao = prorrogaDao;
        this.alumnoTarifaDao = alumnoTarifaDao;
    }

    private static LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public boolean isValidCommitmentDate(Date newDate) {
        return newDate != null && toLocalDate(newDate).isAfter(LocalDate.now());
    }

    @Override
    @Transactional(rollbackFor = ProrrogaValidationException.class)
    public void registerExtension(int alutarId, Date newDate, String comment, int usuId) throws ProrrogaValidationException {
        if (!isValidCommitmentDate(newDate)) {
            throw new ProrrogaValidationException(INVALID_COMMITMENT_DATE_MESSAGE);
        }

        AdProrroga prorroga = new AdProrroga();
        prorroga.setProNumero(String.valueOf(nextProNumero(alutarId)));
        prorroga.setProFecha(newDate);
        prorroga.setProComentario(comment);
        prorroga.setProCreacion(new Timestamp(System.currentTimeMillis()));
        prorroga.setProActivo("1");
        prorroga.setAdAlumnoTarifa(new AdAlumnoTarifa(alutarId));
        prorroga.setTbUsuario(new TbUsuario(usuId));
        prorrogaDao.insertarProrroga(prorroga);

        // The just-registered extension is the active one: the effective deadline becomes its date,
        // regardless of whether it is the maximum among the history (latest registered wins).
        AdAlumnoTarifa tarifa = alumnoTarifaDao.seleccionarUnAlumnoTarifa(alutarId);
        tarifa.setAlutarFechaProrroga(newDate);
        alumnoTarifaDao.actualizarAlumnoTarifa(tarifa);
    }

    /**
     * Next sequence number for a tariff = highest existing active {@code pro_numero} + 1
     * (registration order). Robust to non-contiguous or legacy values; {@code pro_numero} is stored
     * as text, so it is parsed numerically.
     */
    private int nextProNumero(int alutarId) {
        return prorrogaDao.listaProrrogas(alutarId).stream()
                .map(AdProrroga::getProNumero)
                .filter(n -> n != null && n.trim().matches("\\d+"))
                .mapToInt(n -> Integer.parseInt(n.trim()))
                .max().orElse(0) + 1;
    }
}
