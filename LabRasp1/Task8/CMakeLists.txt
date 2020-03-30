cmake_minimum_required(VERSION 3.15)
project(Task8)

find_package(MPI REQUIRED)

include_directories(${MPI_INCLUDE_PATH})
add_executable(Task8 main.c)
target_link_libraries(Task8 ${MPI_LIBRARIES})

if(MPI_COMPILE_FLAGS)
    set_target_properties(Task8 PROPERTIES
            COMPILE_FLAGS "${MPI_COMPILE_FLAGS}")
endif()

if(MPI_LINK_FLAGS)
    set_target_properties(Task8 PROPERTIES
            LINK_FLAGS "${MPI_LINK_FLAGS}")
endif()
