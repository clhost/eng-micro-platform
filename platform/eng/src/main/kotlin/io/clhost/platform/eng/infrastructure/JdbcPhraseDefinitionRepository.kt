package io.clhost.platform.eng.infrastructure

import io.clhost.extension.jackson.jsonDecode
import io.clhost.extension.jackson.jsonEncode
import io.clhost.platform.eng.domain.PhraseDefinition
import io.clhost.platform.eng.domain.PhraseDefinitionRepository
import java.sql.ResultSet
import java.time.OffsetDateTime
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class JdbcPhraseDefinitionRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate
) : PhraseDefinitionRepository {

    val phraseDefinitionJdbcMapper = { rs: ResultSet, _: Int ->
        PhraseDefinition.reflect(
            phrase = rs.getString("phrase"),
            createdAt = rs.getObject("created_at", OffsetDateTime::class.java),
            updatedAt = rs.getObject("updated_at", OffsetDateTime::class.java),
            meanings = jsonDecode(rs.getString("meanings")),
            translations = jsonDecode(rs.getString("translations")),
            tags = jsonDecode(rs.getString("tags"))
        )
    }

    override fun get(phrase: String): PhraseDefinition? {
        val sql = """
            SELECT
                phrase,
                translations,
                meanings,
                tags,
                created_at,
                updated_at
            FROM
                phrase_definition
            WHERE
                phrase = :phrase
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("phrase", phrase)

        return jdbcTemplate.query(sql, params, phraseDefinitionJdbcMapper).firstOrNull()
    }

    override fun create(phraseDefinition: PhraseDefinition) {
        val sql = """
            INSERT INTO phrase_definition (
                phrase,
                translations,
                meanings,
                tags,
                created_at,
                updated_at
            ) VALUES (
                :phrase,
                :translations::JSONB,
                :meanings::JSONB,
                :tags::JSONB,
                :created_at,
                :updated_at
            )
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("phrase", phraseDefinition.phrase)
            .addValue("translations", jsonEncode(phraseDefinition.translations))
            .addValue("meanings", jsonEncode(phraseDefinition.meanings))
            .addValue("tags", jsonEncode(phraseDefinition.tags))
            .addValue("created_at", phraseDefinition.createdAt)
            .addValue("updated_at", phraseDefinition.updatedAt)

        jdbcTemplate.update(sql, params)
    }

    override fun update(phraseDefinition: PhraseDefinition) {
        val sql = """
            UPDATE phrase_definition
            SET
                translations = :translations::JSONB,
                meanings = :meanings::JSONB,
                tags = :tags::JSONB,
                updated_at = :updated_at
            WHERE
                phrase = :phrase
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("phrase", phraseDefinition.phrase)
            .addValue("translations", jsonEncode(phraseDefinition.translations))
            .addValue("meanings", jsonEncode(phraseDefinition.meanings))
            .addValue("tags", jsonEncode(phraseDefinition.tags))
            .addValue("updated_at", phraseDefinition.updatedAt)

        jdbcTemplate.update(sql, params)
    }

    override fun delete(phrase: String) {
        val sql = """
            DELETE
            FROM
                phrase_definition
            WHERE
                phrase = :phrase
        """.trimIndent()

        val params = MapSqlParameterSource()
            .addValue("phrase", phrase)

        jdbcTemplate.update(sql, params)
    }
}
