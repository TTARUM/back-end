import "reflect-metadata";
import { config } from "dotenv";
import { DataSource } from "typeorm";
import { createConnection } from "mysql2/promise";
import { Coupon, entities } from "./entities";
config();
async function initializeEmptyDatabase() {
  const database = process.env.DATABASE_NAME ?? "ttarum";
  const connection = await createConnection({
    host: process.env.DATABASE_HOST ?? "localhost",
    port: Number(process.env.DATABASE_PORT ?? 3306),
    user: process.env.DATABASE_USERNAME ?? "root",
    password: process.env.DATABASE_PASSWORD,
  });
  try {
    await connection.query(
      "CREATE DATABASE IF NOT EXISTS ?? CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci",
      [database],
    );
  } finally {
    await connection.end();
  }
  const db = new DataSource({
    type: "mysql",
    host: process.env.DATABASE_HOST ?? "localhost",
    port: Number(process.env.DATABASE_PORT ?? 3306),
    username: process.env.DATABASE_USERNAME ?? "root",
    password: process.env.DATABASE_PASSWORD,
    database,
    entities,
    synchronize: false,
  });
  await db.initialize();
  try {
    const tables: unknown[] = await db.query("SHOW TABLES");
    if (tables.length)
      throw new Error(
        "db:init only supports an EMPTY database. Existing Spring databases must not be synchronized.",
      );
    await db.synchronize();
    const references: [string, string, string][] = [
      ["normal_member", "member_id", "member"],
      ["oauth_member", "member_id", "member"],
      ["oauth_member", "provider_id", "member_provider"],
      ["item", "category_id", "category"],
      ["wishlist", "member_id", "member"],
      ["wishlist", "item_id", "item"],
      ["cart", "member_id", "member"],
      ["cart", "item_id", "item"],
      ["address", "member_id", "member"],
      ["member_coupon", "member_id", "member"],
      ["member_coupon", "coupon_id", "coupon"],
      ["order", "member_id", "member"],
      ["order_item", "order_id", "order"],
      ["order_item", "item_id", "item"],
      ["review", "member_id", "member"],
      ["review", "order_id", "order"],
      ["review", "item_id", "item"],
      ["review_image", "review_id", "review"],
      ["inquiry", "member_id", "member"],
      ["inquiry", "item_id", "item"],
      ["inquiry_image", "inquiry_id", "inquiry"],
      ["inquiry_answer", "inquiry_id", "inquiry"],
    ];
    for (const [table, column, target] of references)
      await db.query(
        `ALTER TABLE \`${table}\` ADD CONSTRAINT \`fk_${table}_${column}\` FOREIGN KEY (\`${column}\`) REFERENCES \`${target}\`(id)`,
      );
    await db
      .getRepository(Coupon)
      .insert({
        id: 1,
        name: "신규 가입 쿠폰",
        couponStrategy: "PERCENTAGE",
        value: 10,
      });
    console.log("Empty database initialized.");
  } finally {
    await db.destroy();
  }
}
initializeEmptyDatabase().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
