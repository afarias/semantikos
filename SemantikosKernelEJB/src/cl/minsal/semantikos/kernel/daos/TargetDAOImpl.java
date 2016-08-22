package cl.minsal.semantikos.kernel.daos;

import cl.minsal.semantikos.kernel.util.ConnectionBD;
import cl.minsal.semantikos.model.basictypes.BasicTypeDefinition;
import cl.minsal.semantikos.model.basictypes.BasicTypeValue;
import cl.minsal.semantikos.model.relationships.Target;
import cl.minsal.semantikos.model.relationships.TargetDefinition;
import cl.minsal.semantikos.model.relationships.TargetFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.metamodel.BasicType;
import java.sql.*;

import static cl.minsal.semantikos.model.relationships.TargetType.*;
import static java.sql.Types.*;

/**
 * @author Diego Soto
 */
@Stateless
public class TargetDAOImpl implements TargetDAO {

    /**
     * El logger para esta clase
     */
    private static final Logger logger = LoggerFactory.getLogger(TargetDAOImpl.class);

    @EJB
    private TargetFactory targetFactory;

    @Override
    public long createTarget(float floatValue, Timestamp dateValue, String stringValue, boolean booleanValue, int intValue, long idAuxiliary, long idExtern, long idConceptSCT, long idConceptSMTK, long targetType) {
        long target;

        ConnectionBD connect = new ConnectionBD();

        String sql = "{call semantikos.create_target(?,?,?,?,?,?,?,?,?,?)}";

        try (Connection connection = connect.getConnection();
             CallableStatement call = connection.prepareCall(sql)) {

            call.setFloat(1, floatValue);
            call.setTimestamp(2, dateValue);
            call.setString(3, stringValue);
            call.setBoolean(4, booleanValue);
            call.setInt(5, intValue);
            call.setFloat(6, idAuxiliary);
            call.setFloat(7, idExtern);
            call.setFloat(8, idConceptSCT);
            call.setFloat(9, idConceptSMTK);
            call.setFloat(10, targetType);

            call.execute();

            ResultSet rs = call.getResultSet();

            if (rs.next()) {
                target = rs.getLong(1);
                if (target == -1) {
                    String errorMsg = "El target no fue creado";
                    logger.error(errorMsg);
                    throw new EJBException(errorMsg);
                }
            } else {
                String errorMsg = "El target no fue creado";
                logger.error(errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }
            rs.close();
        } catch (SQLException e) {
            throw new EJBException(e);
        }


        return target;
    }

    @Override
    public Target getTargetByID(long idTarget) {

        Target target;
        ConnectionBD connect = new ConnectionBD();
        String sqlQuery = "{call semantikos.get_target_by_id(?)}";
        try (Connection connection = connect.getConnection();
             CallableStatement call = connection.prepareCall(sqlQuery)) {

            /* Se invoca la consulta para recuperar las relaciones */
            call.setLong(1, idTarget);
            call.execute();

            /* Cada Fila del ResultSet trae una relación */
            ResultSet rs = call.getResultSet();
            if (rs.next()) {
                String jsonResult = rs.getString(1);
                target = targetFactory.createTargetFromJSON(jsonResult);
            } else {
                String errorMsg = "Un error imposible acaba de ocurrir";
                logger.error(errorMsg);
                throw new EJBException(errorMsg);
            }
            rs.close();
        } catch (SQLException e) {
            String errorMsg = "Erro al invocar get_relationship_definitions_by_category(" + idTarget + ")";
            logger.error(errorMsg, e);
            throw new EJBException(errorMsg, e);
        }

        return target;
    }

    @Override
    public long persist(Target target, TargetDefinition targetDefinition) {

        ConnectionBD connect = new ConnectionBD();
        /*
         * Parámetros de la función:
         *   1: Valor flotante tipo básico.
         *   2: Valor Timestamp tipo básico.
         *   3: Valor String tipo básico.
         *   4: Valor Booleano tipo básico.
         *   5: Valor entero tipo básico.
         *   6: ID helper table record.
         *   7: id helper Ext
         *   8: ID SCT
         *   9: Concept SMTK
         */
        String sql = "{call semantikos.create_target(?,?,?,?,?,?,?,?,?,?)}";
        long idTarget;

        try (Connection connection = connect.getConnection();
             CallableStatement call = connection.prepareCall(sql)) {

            /* Se fijan de los argumentos por defecto */
            setDefaultValuesForCreateTargetFunction(call);

            /* Almacenar el tipo básico */
            if (targetDefinition.isBasicType()) {
                setTargetCall((BasicTypeValue) target, (BasicTypeDefinition) targetDefinition, call);
                call.setLong(10, BasicType.getIdTargetType());
            }

            /* Almacenar concepto SMTK */
            else if (targetDefinition.isSMTKType()) {
                call.setLong(9, target.getId());
                call.setLong(10, SMTK.getIdTargetType());
            }

            /* Almacenar registro Tabla auxiliar */
            else if (targetDefinition.isHelperTable()) {
                call.setLong(6, target.getId());
                call.setLong(10, HelperTable.getIdTargetType());
            }

            /* Almacenar concepto SCT */
            else if (targetDefinition.isSnomedCTType()) {
                call.setLong(8, target.getId());
                call.setLong(10, SnomedCT.getIdTargetType());
            }


            call.execute();
            ResultSet rs = call.getResultSet();
            if (rs.next()) {
                idTarget = rs.getLong(1);
            } else {
                throw new EJBException("No se obtuvo respuesta de la base de datos, ni una excepción.");
            }
            rs.close();

        } catch (SQLException e) {
            throw new EJBException(e);
        }
        return idTarget;
    }

    private void setTargetCall(BasicTypeValue target, BasicTypeDefinition targetDefinition, CallableStatement call) throws SQLException {

        if (targetDefinition.isDate()) {
            call.setTimestamp(2, (Timestamp) target.getValue());
        } else if (targetDefinition.isFloat()) {
            call.setFloat(1, (Float) target.getValue());
        } else if (targetDefinition.isInteger()) {
            call.setInt(5, (Integer) target.getValue());
        } else if (targetDefinition.isString()) {
            call.setString(3, (String) target.getValue());
        } else if (targetDefinition.isBasicType()) {
            call.setBoolean(4, (Boolean) target.getValue());
        } else {
            throw new EJBException("Tipo Básico no conocido.");
        }
    }

    /**
     * Este método es responsable de fijar los valores por defectos NULOS de la función crear concepto.
     *
     * @throws SQLException
     */
    private void setDefaultValuesForCreateTargetFunction(CallableStatement call) throws SQLException {
        call.setNull(1, FLOAT);
        call.setNull(2, TIMESTAMP);
        call.setNull(3, VARCHAR);
        call.setNull(4, BOOLEAN);
        call.setNull(5, INTEGER);
        call.setNull(6, INTEGER);
        call.setNull(7, INTEGER);
        call.setNull(8, INTEGER);
        call.setNull(9, INTEGER);
        call.setNull(10, INTEGER);
    }

    public long persist(BasicType target, TargetDefinition targetDefinition) {
        System.out.println("Paso por aca!");
        int i = 0;
        i++;
        System.out.println(i);
        return 0;
    }
}
