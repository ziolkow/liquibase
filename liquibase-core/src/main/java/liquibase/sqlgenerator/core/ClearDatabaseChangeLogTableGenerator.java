package liquibase.sqlgenerator.core;

import liquibase.database.Database;
import liquibase.database.ObjectQuotingStrategy;
import liquibase.exception.ValidationErrors;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.ClearDatabaseChangeLogTableStatement;
import liquibase.structure.core.Relation;
import liquibase.structure.core.Table;
import liquibase.util.StringUtil;

public class ClearDatabaseChangeLogTableGenerator extends AbstractSqlGenerator<ClearDatabaseChangeLogTableStatement> {

    @Override
    public ValidationErrors validate(ClearDatabaseChangeLogTableStatement clearDatabaseChangeLogTableStatement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        return new ValidationErrors();
    }

    @Override
    public Sql[] generateSql(ClearDatabaseChangeLogTableStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        String schemaName;
        if (StringUtil.isNotEmpty(statement.getSchemaName())) {
            schemaName = statement.getSchemaName();
        } else {
            schemaName = database.getLiquibaseSchemaName();
        }
        // use LEGACY quoting since we're dealing with system objects
        ObjectQuotingStrategy currentStrategy = database.getObjectQuotingStrategy();
        database.setObjectQuotingStrategy(ObjectQuotingStrategy.LEGACY);
        try {
            return new Sql[] {
                    new UnparsedSql("DELETE FROM " + database.escapeTableName(database.getLiquibaseCatalogName(), schemaName, database.getDatabaseChangeLogTableName()),
                                    getAffectedTable(database, schemaName)) };
        } finally {
            database.setObjectQuotingStrategy(currentStrategy);
        }
    }

    protected Relation getAffectedTable(Database database, String schemaName) {
        return new Table().setName(database.getDatabaseChangeLogTableName()).setSchema(database.getLiquibaseCatalogName(), schemaName);
    }
}
