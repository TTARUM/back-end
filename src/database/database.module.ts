import { Global, Module } from "@nestjs/common";
import { ConfigService } from "@nestjs/config";
import { TypeOrmModule } from "@nestjs/typeorm";
import { entities } from "./entities";
@Global()
@Module({
  imports: [
    TypeOrmModule.forRootAsync({
      inject: [ConfigService],
      useFactory: (c: ConfigService) => ({
        type: "mysql" as const,
        host: c.get("DATABASE_HOST", "localhost"),
        port: Number(c.get("DATABASE_PORT", 3306)),
        username: c.get<string>("DATABASE_USERNAME", "root"),
        password: c.get<string>("DATABASE_PASSWORD"),
        database: c.get<string>("DATABASE_NAME", "ttarum"),
        entities,
        synchronize: false,
        timezone: "Z",
        retryAttempts: 3,
      }),
    }),
  ],
  exports: [TypeOrmModule],
})
export class DatabaseModule {}
