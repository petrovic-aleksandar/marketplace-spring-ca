package me.aco.marketplace_spring_ca.application.usecases.auth.command;

public record UpdateSelfCommand(
        Long id,
        String username,
        boolean updatePassword,
        String password,
        String name,
        String email,
        String phone
) {
        public static UpdateSelfCommand withId(Long id, UpdateSelfCommand command) {
            return new UpdateSelfCommand(
                id,
                command.username(),
                command.updatePassword(),
                command.password(),
                command.name(),
                command.email(),
                command.phone()
            );
        }
}
