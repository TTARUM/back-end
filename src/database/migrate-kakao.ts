import "reflect-metadata";
import { config } from "dotenv";
import { createConnection } from "mysql2/promise";
config();
async function migrate() {
  const db = await createConnection({
    host: process.env.DATABASE_HOST ?? "localhost",
    port: Number(process.env.DATABASE_PORT ?? 3306),
    user: process.env.DATABASE_USERNAME ?? "root",
    password: process.env.DATABASE_PASSWORD,
    database: process.env.DATABASE_NAME ?? "ttarum",
  });
  try {
    const [columns] = await db.query<any[]>("SHOW COLUMNS FROM oauth_member");
    if (!columns.some((c) => c.Field === "email"))
      await db.query(
        "ALTER TABLE oauth_member ADD COLUMN email varchar(320) NULL",
      );
    if (!columns.some((c) => c.Field === "provider_subject"))
      await db.query(
        "ALTER TABLE oauth_member ADD COLUMN provider_subject varchar(100) NULL",
      );
    const [indexes] = await db.query<any[]>(
      "SHOW INDEX FROM oauth_member WHERE Key_name = 'uq_oauth_provider_subject'",
    );
    if (!indexes.length)
      await db.query(
        "ALTER TABLE oauth_member ADD UNIQUE INDEX uq_oauth_provider_subject (provider_id, provider_subject)",
      );
    const [providers] = await db.query<any[]>(
      "SELECT id FROM member_provider WHERE LOWER(name) = 'kakao'",
    );
    if (providers.length > 1)
      throw new Error("Multiple Kakao providers require manual review.");
    if (!providers.length)
      await db.query("INSERT INTO member_provider (name) VALUES ('KAKAO')");
    else
      await db.query("UPDATE member_provider SET name = 'KAKAO' WHERE id = ?", [
        providers[0].id,
      ]);
    console.log(
      "Kakao schema verified: email, provider_subject, unique identity, KAKAO provider.",
    );
  } finally {
    await db.end();
  }
}
migrate().catch(() => {
  console.error(
    "Kakao migration failed. Check database availability and schema; no tables were dropped.",
  );
  process.exitCode = 1;
});
