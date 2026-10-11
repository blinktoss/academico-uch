package net.uch.cursolibre.jsf;

import net.uch.common.AbstractBaseMbViewTree;
import net.uch.common.ServiceFinder;
import net.uch.cursolibre.dao.*;
import net.uch.cursolibre.jsf.beans.ObjNodeCLGnral;
import net.uch.mapping.ClArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created for sistema_academico on 24/02/2024.
 *
 * @author Cesardl
 */
public class BaseMbViewTreeCL extends AbstractBaseMbViewTree {

    private static final Logger LOG = LoggerFactory.getLogger(BaseMbViewTreeCL.class);

    private List<ObjNodeCLGnral> nodos;

    public List<ObjNodeCLGnral> getNodos() {
        return nodos;
    }

    public void setNodos(List<ObjNodeCLGnral> nodos) {
        this.nodos = nodos;
    }

    public String armarEstructura() {
        LOG.debug("Armando estructura arbol");
        var estructuraArea = new StringBuilder();
        var estructuraModulo = new StringBuilder();
        var estructuraCurso = new StringBuilder();
        var estructuraTaller = new StringBuilder();
        HSAreaDAO daoA = ServiceFinder.findBean("springHibernateDaoCLArea", HSAreaDAO.class);
        HSModuloDAO daoM = ServiceFinder.findBean("springHibernateDaoCLModulo", HSModuloDAO.class);
        HSCursoCLDAO daoC = ServiceFinder.findBean("springHibernateDaoCLCurso", HSCursoCLDAO.class);
        HSTallerAperturadoDAO daoTA = ServiceFinder.findBean("springHibernateDaoCLTallerAperturado", HSTallerAperturadoDAO.class);
        List<ClArea> areas = daoA.seleccionarArea("");

        // Prefetch modules, courses and opened workshops for the whole tree in one query per level
        // (instead of one query per parent node - the same N+1 already fixed in the academica trees),
        // grouped in-memory by parent id. Rows come back ordered by (parent, own id), so the tree
        // structure string and the node ids are byte-identical to the per-parent-query version.
        List<Integer> areaIds = new ArrayList<>();
        for (ClArea area : areas) {
            areaIds.add(area.getAreId());
        }
        Map<Integer, List<CLModuloRow>> modulosPorArea = new HashMap<>();
        List<Integer> moduloIds = new ArrayList<>();
        for (CLModuloRow row : areaIds.isEmpty() ? Collections.<CLModuloRow>emptyList() : daoM.findModulosByAreas(areaIds)) {
            modulosPorArea.computeIfAbsent(row.getAreaId(), k -> new ArrayList<>()).add(row);
            moduloIds.add(row.getModuloId());
        }
        Map<Integer, List<CLCursoRow>> cursosPorModulo = new HashMap<>();
        List<Integer> cursoIds = new ArrayList<>();
        for (CLCursoRow row : moduloIds.isEmpty() ? Collections.<CLCursoRow>emptyList() : daoC.findCursosByModulos(moduloIds)) {
            cursosPorModulo.computeIfAbsent(row.getModuloId(), k -> new ArrayList<>()).add(row);
            cursoIds.add(row.getCursoId());
        }
        Map<Integer, List<CLTallerAperturadoRow>> talleresPorCurso = new HashMap<>();
        for (CLTallerAperturadoRow row : cursoIds.isEmpty() ? Collections.<CLTallerAperturadoRow>emptyList() : daoTA.findOpenedWorkshopsByCourses(cursoIds)) {
            talleresPorCurso.computeIfAbsent(row.getCursoId(), k -> new ArrayList<>()).add(row);
        }

        List<ObjNodeCLGnral> lnodo = new ArrayList<>();
        for (var i = 0; i < areas.size(); i++) {
            List<CLModuloRow> modulos = modulosPorArea.getOrDefault(areas.get(i).getAreId(), Collections.emptyList());
            estructuraArea.append(i + 1).append("=").append(areas.get(i).getAreDescripcion()).append("\n");
            for (var j = 0; j < modulos.size(); j++) {
                List<CLCursoRow> cursos = cursosPorModulo.getOrDefault(modulos.get(j).getModuloId(), Collections.emptyList());
                for (var k = 0; k < cursos.size(); k++) {
                    List<CLTallerAperturadoRow> talleres = talleresPorCurso.getOrDefault(cursos.get(k).getCursoId(), Collections.emptyList());
                    if (!talleres.isEmpty()) {
                        estructuraModulo.append(i + 1).append(".").append(j + 1).append("=").append(modulos.get(j).getModuloDescripcion()).append("\n");
                        estructuraCurso.append(i + 1).append(".").append(j + 1).append(".").append(k + 1).append("=").append(cursos.get(k).getCursoNombre()).append("\n");
                        for (var l = 0; l < talleres.size(); l++) {
                            estructuraTaller.append(i + 1).append(".").append(j + 1).append(".").append(k + 1).append(".").append(l + 1).append("=").append(talleres.get(l).getTallerAperturadoDescripcion()).append("\n");

                            var node = new ObjNodeCLGnral();
                            node.setId((i + 1) + ":" + (j + 1) + ":" + (k + 1) + ":" + (l + 1));
                            node.setTalapeId(talleres.get(l).getTallerAperturadoId());
                            node.setTalapeNombre(talleres.get(l).getTallerAperturadoDescripcion());
                            node.setCurId(cursos.get(k).getCursoId());
                            node.setCurNombre(cursos.get(k).getCursoNombre());
                            node.setModId(modulos.get(j).getModuloId());
                            node.setModNombre(modulos.get(j).getModuloDescripcion());
                            lnodo.add(node);
                        }
                    }
                }
            }
        }
        this.setNodos(lnodo);
        return estructuraArea + estructuraModulo.toString() + estructuraCurso + estructuraTaller;
    }

    public ObjNodeCLGnral buscarNodo(String id) {
        return nodos.stream().filter(nodo -> nodo.getId().equalsIgnoreCase(id))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
